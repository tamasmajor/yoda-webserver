package com.major.yodaserver.requestprocessor.factory;

import java.io.File;
import java.net.Socket;

import com.major.yodaserver.requestprocessor.RequestProcessor;
import com.major.yodaserver.requestprocessor.YodaRequestProcessor;

public class YodaRequestProcessorFactory implements RequestProcessorFactory {
    @Override
    public RequestProcessor createRequestProcessor(File rootDir, Socket connection) {
        return new YodaRequestProcessor(rootDir, connection);
    }
}
