package com.major.yodaserver.requestprocessor;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.major.yodaserver.common.Header;
import com.major.yodaserver.common.StatusCode;


public class AcknowledgementRequestProcessor implements RequestProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AcknowledgementRequestProcessor.class);

    private Socket connection;
    private BufferedOutputStream raw;
    private Writer headerWriter;

    private AcknowledgementRequestProcessor(Socket connection, OutputStream out) {
        this.connection = connection;
        raw = new BufferedOutputStream(out);
        headerWriter = new OutputStreamWriter(raw);
    }

    public static AcknowledgementRequestProcessor newInstance(Socket connection) {
        try {
            return new AcknowledgementRequestProcessor(connection, connection.getOutputStream());
        } catch (IOException ioe) {
            throw new RuntimeException("Could not create the request processor", ioe);
        }
    }

    public void process() {
        try {
            logger.info(connection.getRemoteSocketAddress().toString());
            ResponseMessageHeader response = new ResponseMessageHeader.Builder(StatusCode.OK)
                                                                      .addHeader(Header.CONTENT_LENGTH, 0)
                                                                      .build();
            headerWriter.write(response.asHttpResponse());
            headerWriter.flush();
            raw.flush();
        } catch (IOException e) {
            // client disconnected, nothing to do
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
                logger.error("Could not close the connection {}", ex);
            }
        }
    }

}
