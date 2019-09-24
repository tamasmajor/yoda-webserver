package com.major.yodaserver.requestprocessor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

public abstract class RequestProcessor implements Runnable {
    protected static final String RESPONSE_LINE_TERMINATOR = "\r\n";

    protected final File rootDir;
    protected final Socket connection;

    public RequestProcessor(File rootDir, Socket connection) {
        this.rootDir = rootDir;
        this.connection = connection;
    }

    protected byte[] asResponseLine(String line) {
        return (line + RESPONSE_LINE_TERMINATOR).getBytes();
    }

    protected String readRequestLine() {
        try {
            // no try with resource as we don't want to close the input stream yet (would close the connection)
            Reader reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()), "ASCII");
            StringBuilder requestLine = new StringBuilder();
            while (true) {
                int c = reader.read();
                if (c == '\r' || c == '\n') break;
                requestLine.append((char) c);
            }
            return requestLine.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not read the request line", e);
        }
    }
}
