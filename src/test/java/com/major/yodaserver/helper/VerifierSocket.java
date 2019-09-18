package com.major.yodaserver.helper;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

public class VerifierSocket extends Socket {
    private ByteArrayOutputStream written = new ByteArrayOutputStream();

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return mock(SocketAddress.class);
    }

    @Override
    public ByteArrayOutputStream getOutputStream() {
        return written;
    }

    public List<String> getWrittenLines() {
        return Arrays.asList(written.toString().split("(?<=\r\n)"));
    }
}
