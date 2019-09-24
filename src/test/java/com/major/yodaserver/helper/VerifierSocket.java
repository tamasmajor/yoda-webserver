package com.major.yodaserver.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;

public class VerifierSocket extends Socket {
    private StringBuffer request;
    private ByteArrayOutputStream response;

    public VerifierSocket() {
        request = new StringBuffer();
        this.response = new ByteArrayOutputStream();
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return new SocketAddress() {
            @Override
            public String toString() {
                return "verifier-socket-address";
            }
        };
    }

    @Override
    public ByteArrayInputStream getInputStream() {
        return new ByteArrayInputStream(request.toString().getBytes());
    }

    @Override
    public ByteArrayOutputStream getOutputStream() {
        return response;
    }

    public void addRequestLine(String line) {
        request.append(line);
        request.append("\r\n");
    }

    public List<String> getResponseLines() {
        return Arrays.asList(response.toString().split("(?<=\r\n)"));
    }
}
