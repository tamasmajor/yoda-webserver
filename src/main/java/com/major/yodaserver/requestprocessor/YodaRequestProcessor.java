package com.major.yodaserver.requestprocessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.major.yodaserver.connection.SocketReader;

public class YodaRequestProcessor extends RequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(YodaRequestProcessor.class);
    private static final String NOT_SUPPORTED_MESSAGE_BODY = "<HTML><HEAD><TITLE>Not yet supported</TITLE></HEAD>" +
                                                             "<BODY>501 - Not yet supported</BODY></HTML>";

    public YodaRequestProcessor(File rootDir, Socket connection) {
        super(rootDir, connection);
    }

    @Override
    public void run() {
        try {
            logger.info("Connection from: {}", connection.getRemoteSocketAddress().toString());

            SocketReader socketReader = new SocketReader(connection);
            BufferedOutputStream response = new BufferedOutputStream(connection.getOutputStream());

            String requestMethod = socketReader.getRequestMethod();

            if (requestMethod.equals("GET")) {
                String uri = socketReader.getRequestUri();
                String contentType = URLConnection.getFileNameMap().getContentTypeFor(uri);
                // TODO: make sure clients cannot navigate outside of the document root with ".."
                File requestedResource = new File(rootDir, uri.substring(1, uri.length()));
                byte[] resource = Files.readAllBytes(requestedResource.toPath());
                response.write(asResponseLine("HTTP/1.1 200 OK"));
                response.write(asResponseLine("Server: YodaServer 0.0.1"));
                response.write(asResponseLine("Content-Length: " + resource.length));
                response.write(asResponseLine("Content-Type: " + contentType));
                response.write(asResponseLine(""));
                response.write(resource);
                response.flush();
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

    private String constructNotSupportedResponse() {
        return String.join(RESPONSE_LINE_TERMINATOR, "HTTP/1.1 501 Not Implemented",
                                                     "Server: YodaServer 0.0.1",
                                                     "Content-Length: " + NOT_SUPPORTED_MESSAGE_BODY.getBytes().length,
                                                     "Content-type: text/html",
                                                     "",
                                                     NOT_SUPPORTED_MESSAGE_BODY);
    }


}
