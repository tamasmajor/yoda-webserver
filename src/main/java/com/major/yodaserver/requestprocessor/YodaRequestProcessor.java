package com.major.yodaserver.requestprocessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.major.yodaserver.common.CommonHtmlPage;
import com.major.yodaserver.common.Header;
import com.major.yodaserver.common.Method;
import com.major.yodaserver.common.MimeType;
import com.major.yodaserver.common.StatusCode;
import com.major.yodaserver.connection.SocketReader;
import com.major.yodaserver.requestprocessor.plugin.DirectoryExplorer;

public class YodaRequestProcessor implements RequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(YodaRequestProcessor.class);
    private static final String INDEX_FILE = "index.html";

    private final DirectoryExplorer directoryExplorer;
    private final File rootDirectory;

    private Socket connection;
    private BufferedOutputStream raw;
    private Writer headerWriter;

    private YodaRequestProcessor(Socket connection, File rootDir, DirectoryExplorer dirExplorer, OutputStream out) {
        this.connection = connection;
        rootDirectory = rootDir;
        directoryExplorer = dirExplorer;
        raw = new BufferedOutputStream(out);
        headerWriter = new OutputStreamWriter(raw);
    }

    public static YodaRequestProcessor newInstance(File rootDir, Socket connection, DirectoryExplorer dirExplorer) {
        try {
            return new YodaRequestProcessor(connection, rootDir, dirExplorer, connection.getOutputStream());
        } catch (IOException ioe) {
            throw new RuntimeException("Could not create the request processor", ioe);
        }
    }

    @Override
    public void process() {
        logger.info(connection.getRemoteSocketAddress().toString());
        try {
            SocketReader socketReader = new SocketReader(connection);
            String requestUri = URLDecoder.decode(socketReader.getRequestUri(), StandardCharsets.UTF_8.toString());
            String requestMethod = socketReader.getRequestMethod();
            logger.info("Processing {} request for: {}", requestMethod, requestUri);

            if (Method.GET == Method.methodByName(requestMethod)) {
                handleGet(requestUri);
            } else {
                respondWith(CommonHtmlPage.NOT_IMPLEMENTED);
            }
        } catch (Exception e) {
            logger.warn("Error during the request processing from " + connection.getRemoteSocketAddress(), e);
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
                logger.error("Could not close the connection {}", ex);
            }
        }
    }

    private void handleGet(String requestUri) throws IOException {
        File requestedResource = new File(rootDirectory, requestUri.substring(1, requestUri.length()));
        if (requestedResourceAccessible(requestedResource)) {
            if (requestedResource.isDirectory()) {
                handleRequestForDirectory(requestedResource, requestUri);
            } else {
                handleRequestForFile(requestedResource);
            }
        } else {
            respondWith(CommonHtmlPage.NOT_FOUND);
        }
    }

    private void handleRequestForDirectory(File requestedResource, String requestUri) throws IOException {
        if (hasIndexFile(requestedResource)) {
            respontWithIndexFile(requestUri, requestedResource);
        } else {
            renderDirectoryContent(requestedResource);
        }
    }

    private void respontWithIndexFile(String uri, File requestedResource) throws IOException {
        if (uri.substring(uri.length() - 1).equals("/")) {
            renderIndexFile(requestedResource);
        } else {
            redirectAddingTrailingSlash(uri);
        }
        headerWriter.flush();
    }

    private void renderIndexFile(File requestedResource) throws IOException {
        File indexFile = new File(requestedResource, INDEX_FILE);
        byte[] resource = Files.readAllBytes(indexFile.toPath());
        ResponseMessageHeader response = new ResponseMessageHeader.Builder(StatusCode.OK)
                .addHeader(Header.CONTENT_TYPE, MimeType.HTML.getMimeType())
                .addHeader(Header.CONTENT_LENGTH, resource.length)
                .build();

        headerWriter.write(response.asHttpResponse());
        headerWriter.flush();
        raw.write(resource);
        raw.flush();
    }

    private void redirectAddingTrailingSlash(String uri) throws IOException {
        ResponseMessageHeader response = new ResponseMessageHeader.Builder(StatusCode.FOUND)
                .addHeader(Header.CONTENT_LENGTH, 0)
                .addHeader(Header.LOCATION, uri + "/")
                .build();
        headerWriter.write(response.asHttpResponse());
        headerWriter.flush();
        raw.flush();
    }

    private void renderDirectoryContent(File requestedResource) throws IOException {
        String data = directoryExplorer.renderPage(rootDirectory, requestedResource);
        ResponseMessageHeader responseHeaders = new ResponseMessageHeader.Builder(StatusCode.OK)
                .addHeader(Header.CONTENT_TYPE, MimeType.HTML.getMimeType())
                .addHeader(Header.CONTENT_LENGTH, data.getBytes().length)
                .build();
        headerWriter.write(responseHeaders.asHttpResponse());
        headerWriter.flush();
        raw.write(data.getBytes());
        raw.flush();
    }

    private void handleRequestForFile(File requestedResource) throws IOException {
        byte[] resource = Files.readAllBytes(requestedResource.toPath());
        ResponseMessageHeader responseHeaders = new ResponseMessageHeader.Builder(StatusCode.OK)
                .addHeader(Header.CONTENT_TYPE, getContentTypeFor(requestedResource.getName()))
                .addHeader(Header.CONTENT_LENGTH, resource.length)
                .build();
        headerWriter.write(responseHeaders.asHttpResponse());
        headerWriter.flush();
        raw.write(resource);
        raw.flush();
    }

    private String getContentTypeFor(String uri) {
        String contentType = URLConnection.getFileNameMap().getContentTypeFor(uri);
        if (contentType == null ) {
            contentType = MimeType.typeForExtension(uri);
        }
        return contentType;
    }

    private boolean hasIndexFile(File requestedResource) {
        File file = new File(requestedResource, INDEX_FILE);
        return file.exists();
    }

    private boolean requestedResourceAccessible(File requestedResource) throws IOException {
        return requestedResource.canRead() &&
               requestedResource.getCanonicalPath().startsWith(rootDirectory.getCanonicalPath());
    }

    private void respondWith(CommonHtmlPage commonHtmlPage) throws IOException {
        byte[] response = commonHtmlPage.getHtml().getBytes();
        headerWriter.write(new ResponseMessageHeader.Builder(commonHtmlPage.getStatusCode())
                                                    .addHeader(Header.CONTENT_LENGTH, response.length)
                                                    .addHeader(Header.CONTENT_TYPE, MimeType.HTML.getMimeType())
                                                    .build()
                                                    .asHttpResponse());
        headerWriter.flush();
        raw.write(response);
        raw.flush();
    }
}
