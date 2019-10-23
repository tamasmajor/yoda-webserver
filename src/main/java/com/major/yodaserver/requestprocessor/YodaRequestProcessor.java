package com.major.yodaserver.requestprocessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.major.yodaserver.connection.SocketReader;
import com.major.yodaserver.requestprocessor.plugin.DirectoryExplorer;

public class YodaRequestProcessor extends RequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(YodaRequestProcessor.class);
    protected static final String NOT_SUPPORTED_MESSAGE_BODY = "<HTML><HEAD><TITLE>Not yet supported</TITLE></HEAD>" +
                                                               "<BODY>501 - Not yet supported</BODY></HTML>";
    protected static final String NOT_FOUND_MESSAGE_BODY = "<HTML><HEAD><TITLE>Not found</TITLE></HEAD>" +
                                                           "<BODY>404 - File not found</BODY></HTML>";

    private final DirectoryExplorer directoryExplorer;

    public YodaRequestProcessor(File rootDir, Socket connection, DirectoryExplorer directoryExplorer) {
        super(rootDir, connection);
        this.directoryExplorer = directoryExplorer;
    }

    @Override
    public void run() {
        try {
            logger.info("Connection from: {}", connection.getRemoteSocketAddress().toString());

            SocketReader socketReader = new SocketReader(connection);
            BufferedOutputStream response = new BufferedOutputStream(connection.getOutputStream());

            String requestMethod = socketReader.getRequestMethod();

            if (requestMethod.equals("GET")) {
                String uri = URLDecoder.decode(socketReader.getRequestUri(), StandardCharsets.UTF_8.toString());
                String contentType = URLConnection.getFileNameMap().getContentTypeFor(uri);
                File requestedResource = new File(rootDir, uri.substring(1, uri.length()));

                if (requestedResourceAccessible(requestedResource, rootDir)) {
                    if (requestedResource.isDirectory()) {
                        String data = directoryExplorer.renderPage(rootDir, requestedResource);
                        response.write(asResponseLine("HTTP/1.1 200 OK"));
                        response.write(asResponseLine("Server: YodaServer 0.0.1"));
                        response.write(asResponseLine("Content-Length: " + data.length()));
                        response.write(asResponseLine("Content-Type: text/html"));
                        response.write(asResponseLine(""));
                        response.write(data.getBytes());
                    } else {
                        byte[] resource = Files.readAllBytes(requestedResource.toPath());
                        response.write(asResponseLine("HTTP/1.1 200 OK"));
                        response.write(asResponseLine("Server: YodaServer 0.0.1"));
                        response.write(asResponseLine("Content-Length: " + resource.length));
                        response.write(asResponseLine("Content-Type: " + contentType));
                        response.write(asResponseLine(""));
                        response.write(resource);
                    }
                    response.flush();
                } else {
                    response.write(constructFileNotFoundResponse().getBytes());
                    response.flush();
                }
            } else {
                response.write(constructNotSupportedResponse().getBytes());
                response.flush();
            }
        } catch (IOException e) {
            logger.warn("Error during the request processing from " + connection.getRemoteSocketAddress(), e);
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
                logger.error("Could not close the connection {}", ex);
            }
        }
    }

    private boolean requestedResourceAccessible(File requestedResource, File rootDir) throws IOException {
        return requestedResource.canRead() && requestedResource.getCanonicalPath().startsWith(rootDir.getCanonicalPath());
    }

    private String constructNotSupportedResponse() {
        return String.join(RESPONSE_LINE_TERMINATOR, "HTTP/1.1 501 Not Implemented",
                                                     "Server: YodaServer 0.0.1",
                                                     "Content-Length: " + NOT_SUPPORTED_MESSAGE_BODY.getBytes().length,
                                                     "Content-type: text/html",
                                                     "",
                                                     NOT_SUPPORTED_MESSAGE_BODY);
    }

    private String constructFileNotFoundResponse() {
        return String.join(RESPONSE_LINE_TERMINATOR, "HTTP/1.1 404 File Not Found",
                                                     "Server: YodaServer 0.0.1",
                                                     "Content-Length: " + NOT_FOUND_MESSAGE_BODY.getBytes().length,
                                                     "Content-type: text/html",
                                                     "",
                                                     NOT_FOUND_MESSAGE_BODY);
    }


}
