package com.major.yodaserver.requestprocessor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.major.yodaserver.common.Header;

public class ResponseMessageHeader {
    protected static final String LINE_TERMINATOR = "\r\n";

    private String httpVersion;
    private StatusCode statusCode;
    private Map<String, String> headers;

    private ResponseMessageHeader(Builder builder) {
        httpVersion = builder.httpVersion;
        statusCode = builder.statusCode;
        headers = builder.headers;
    }

    public static class Builder {
        private final String httpVersion = "HTTP/1.1";
        private final StatusCode statusCode;
        private final Map<String, String> headers = new HashMap<>();

        public Builder(StatusCode statusCode) {
            this.statusCode = statusCode;
            headers.put(Header.SERVER.getKey(), "YodaServer 0.0.1");
        }

        public Builder addHeader(String headerName, String headerValue) {
            headers.put(headerName, headerValue);
            return this;
        }

        public Builder addHeader(Header header, String headerValue) {
            return addHeader(header.getKey(), headerValue);
        }

        public Builder addHeader(String headerName, int headerValue) {
            headers.put(headerName, String.valueOf(headerValue));
            return this;
        }

        public Builder addHeader(Header header, int headerValue) {
            return addHeader(header.getKey(), headerValue);
        }

        public ResponseMessageHeader build() {
            return new ResponseMessageHeader(this);
        }
    }

    public String asHttpResponse() {
        StringBuffer responseMessageHeaders = new StringBuffer();
        responseMessageHeaders.append(httpVersion).append(" ")
                              .append(statusCode.getStatusCode()).append(" ")
                              .append(statusCode.getReasonPhrase()).append(LINE_TERMINATOR);

        headers.putIfAbsent(Header.DATE.getKey(), String.valueOf(new Date()));
        headers.entrySet().stream()
                          .map(entry -> entry.getKey() + ": " + entry.getValue() + LINE_TERMINATOR)
                          .forEach(responseMessageHeaders::append);

        responseMessageHeaders.append(LINE_TERMINATOR);
        return responseMessageHeaders.toString();
    }
}