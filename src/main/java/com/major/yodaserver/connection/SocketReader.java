package com.major.yodaserver.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReader {
    private static final String CHARSET = "ASCII";
    private final BufferedReader reader;

    private boolean requestLineProcessed;
    private RequestLine requestLine;

    public SocketReader(Socket connection) throws IOException {
        reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(connection.getInputStream()), CHARSET));
    }

    public String getRequestMethod() throws IOException {
        return obtainRequestLine().getMethod();
    }

    public String getRequestUri() throws IOException {
        return obtainRequestLine().getRequestUri();
    }

    public String getHttpVersion() throws IOException {
        return obtainRequestLine().getHttpVersion();
    }

    private RequestLine obtainRequestLine() throws IOException {
        if (!requestLineProcessed) {
            requestLine = RequestLine.from(reader.readLine());
            requestLineProcessed = true;
        }
        return requestLine;
    }

    private static class RequestLine {
        private final String method;
        private final String requestUri;
        private final String httpVersion;

        private RequestLine(String method, String requestUri, String httpVersion) {
            this.method = method;
            this.requestUri = requestUri;
            this.httpVersion = httpVersion;
        }
        
        public static RequestLine from(String requestLine) {
            String[] token = requestLine.split("\\s+");
            if (token.length != 3) {
                throw new IllegalArgumentException("Request-Line contains invalid number of tokens");
            }
            return new RequestLine(token[0], token[1], token[2]);
        }

        public String getMethod() {
            return method;
        }

        public String getRequestUri() {
            return requestUri;
        }

        public String getHttpVersion() {
            return httpVersion;
        }
    }
}
