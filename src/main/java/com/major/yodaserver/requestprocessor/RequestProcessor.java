package com.major.yodaserver.requestprocessor;

import java.net.Socket;

public abstract class RequestProcessor implements Runnable {
    protected Socket connection;

    public RequestProcessor(Socket connection) {
        this.connection = connection;
    }
}
