package com.major.yodaserver.common;

public enum Header {
    DATE("Date"),
    SERVER("Server"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location");

    private String key;

    Header(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
