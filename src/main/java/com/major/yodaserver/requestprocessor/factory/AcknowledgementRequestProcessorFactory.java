package com.major.yodaserver.requestprocessor.factory;

import java.io.File;
import java.net.Socket;

import com.major.yodaserver.requestprocessor.AcknowledgementRequestProcessor;
import com.major.yodaserver.requestprocessor.RequestProcessor;

public class AcknowledgementRequestProcessorFactory implements RequestProcessorFactory {
    @Override
    public RequestProcessor createRequestProcessor(File rootDir, Socket connection) {
        return AcknowledgementRequestProcessor.newInstance(connection);
    }
}
