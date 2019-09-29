package com.major.yodaserver.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerifierSocket extends Socket {
    private final byte[] BODY_SEPARATOR_SEQUENCE = new byte[] { '\r', '\n', '\r', '\n' };

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

    public List<String> getHeaderLines() {
        List<String> lines = new ArrayList<>();

        byte[] bytes = response.toByteArray();
        int responseBodySeparator = findResponseBodySeparator(bytes);

        if (responseBodySeparator < 0) {
            String[] headerLines = new String(bytes).split("(?<=\\r\\n)");
            Arrays.stream(headerLines).map(s -> s.replace("\r\n", "")).forEach(lines::add);
        } else {
            byte[] headerBytes = Arrays.copyOfRange(bytes, 0, responseBodySeparator);
            String[] headerLines = new String(headerBytes).split("(?<=\\r\\n)");
            Arrays.stream(headerLines).map(s -> s.replace("\r\n", "")).forEach(lines::add);
            lines.add(""); // the empty separator line is needed in this case
        }

        return lines;
    }

    public String bodyAsString() {
        String body = null;
        byte[] bodyBytes = bodyAsBytes();
        if (bodyBytes != null) {
            body = new String(bodyBytes);
        }
        return body;
    }

    public byte[] bodyAsBytes() {
        byte[] body = null;
        byte[] bytes = response.toByteArray();
        int responseBodySeparator = findResponseBodySeparator(bytes);
        if (responseBodySeparator >= 0 && responseBodySeparator + 4 < bytes.length) {
            body = Arrays.copyOfRange(bytes, responseBodySeparator + 4, bytes.length);
        }
        return body;
    }

    public int findResponseBodySeparator(byte[] response) {
        // TODO: refactor
        for (int i = 0; i <= response.length - BODY_SEPARATOR_SEQUENCE.length; i++) {
            boolean found = true;
            for (int j = 0; j < BODY_SEPARATOR_SEQUENCE.length; j++) {
                if (response[i + j] != BODY_SEPARATOR_SEQUENCE[j]) {
                    found = false;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

}
