package com.major.yodaserver.requestprocessor.factory;

import java.io.File;
import java.net.Socket;

import com.major.yodaserver.requestprocessor.RequestProcessor;
import com.major.yodaserver.requestprocessor.YodaRequestProcessor;
import com.major.yodaserver.requestprocessor.plugin.DirectoryExplorer;
import com.major.yodaserver.requestprocessor.plugin.SimpleDirectoryExplorer;

public class YodaRequestProcessorFactory implements RequestProcessorFactory {

    private DirectoryExplorer directoryExplorer;

    public YodaRequestProcessorFactory() {
        this(new SimpleDirectoryExplorer());
    }

    public YodaRequestProcessorFactory(DirectoryExplorer directoryExplorer) {
        this.directoryExplorer = directoryExplorer;
    }

    @Override
    public RequestProcessor createRequestProcessor(File rootDir, Socket connection) {
        return new YodaRequestProcessor(rootDir, connection, directoryExplorer);
    }
}
