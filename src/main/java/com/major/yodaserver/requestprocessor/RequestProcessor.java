package com.major.yodaserver.requestprocessor;

import java.io.File;
import java.net.Socket;

public abstract class RequestProcessor implements Runnable {
    protected final File rootDir;
    protected final Socket connection;

    public RequestProcessor(File rootDir, Socket connection) {
        this.rootDir = rootDir;
        this.connection = connection;
    }
}
