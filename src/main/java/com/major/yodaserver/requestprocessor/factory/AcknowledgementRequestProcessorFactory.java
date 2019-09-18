package com.major.yodaserver.requestprocessor.factory;

import java.net.Socket;

import com.major.yodaserver.requestprocessor.AcknowledgementRequestProcessor;
import com.major.yodaserver.requestprocessor.RequestProcessor;

public class AcknowledgementRequestProcessorFactory implements RequestProcessorFactory {
    @Override
    public RequestProcessor createRequestProcessor(Socket connection) {
        return new AcknowledgementRequestProcessor(connection);
    }
}
