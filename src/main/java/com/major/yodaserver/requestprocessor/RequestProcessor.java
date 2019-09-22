package com.major.yodaserver.requestprocessor;

import java.io.File;
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
}
