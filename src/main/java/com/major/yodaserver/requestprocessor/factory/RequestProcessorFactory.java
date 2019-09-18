package com.major.yodaserver.requestprocessor.factory;

import java.net.Socket;

import com.major.yodaserver.requestprocessor.RequestProcessor;

public interface RequestProcessorFactory {
    RequestProcessor createRequestProcessor(Socket connection);
}
