package com.major.yodaserver.requestprocessor.factory;

import java.io.File;
import java.net.Socket;

import com.major.yodaserver.requestprocessor.RequestProcessor;

public interface RequestProcessorFactory {
    RequestProcessor createRequestProcessor(File rootDir, Socket connection);
}
