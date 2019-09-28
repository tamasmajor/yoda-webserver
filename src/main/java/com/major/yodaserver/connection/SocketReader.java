package com.major.yodaserver.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReader {
    private static final String CHARSET = "ASCII";
    private final BufferedReader reader;

    public SocketReader(Socket connection) throws IOException {
        reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(connection.getInputStream()), CHARSET));
    }

    public String getMethod() throws IOException {
        return reader.readLine().split("\\s+")[0];
    }


    public String getRequestUri() throws IOException {
        return reader.readLine().split("\\s+")[1];
    }

    public String getHttpVersion() throws IOException {
        return reader.readLine().split("\\s+")[2];
    }
}
