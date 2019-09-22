package com.major.yodaserver.requestprocessor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YodaRequestProcessor extends RequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(YodaRequestProcessor.class);
    private static final String LINE_TERMINATOR = "\r\n";

    public YodaRequestProcessor(File rootDir, Socket connection) {
        super(rootDir, connection);
    }

    @Override
    public void run() {
        try {
            logger.info(connection.getRemoteSocketAddress().toString());
            BufferedOutputStream response = new BufferedOutputStream(connection.getOutputStream());
            response.write(asBytes("HTTP/1.1 200 OK"));
            response.write(asBytes("Server: YodaServer 0.0.1"));
            response.write(asBytes("Content-Length: 0"));
            response.write(asBytes(""));
            response.flush();
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

    private byte[] asBytes(String line) {
        return (line + LINE_TERMINATOR).getBytes();
    }
}
