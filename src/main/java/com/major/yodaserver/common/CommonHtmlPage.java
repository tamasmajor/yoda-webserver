package com.major.yodaserver.common;

import com.major.yodaserver.requestprocessor.StatusCode;

public enum CommonHtmlPage {
    NOT_IMPLEMENTED(StatusCode.NOT_IMPLEMENTED, "<HTML><HEAD><TITLE>Not supported</TITLE></HEAD<BODY>501 - Not supported</BODY></HTML>"),
    NOT_FOUND(StatusCode.NOT_FOUND, "<HTML><HEAD><TITLE>Not found</TITLE></HEAD><BODY>404 - File not found</BODY></HTML>");

    private final StatusCode statusCode;
    private final String html;

    CommonHtmlPage(StatusCode statusCode, String html) {
        this.statusCode = statusCode;
        this.html = html;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getHtml() {
        return html;
    }
}
