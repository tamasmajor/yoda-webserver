package com.major.yodaserver;

import javax.net.ServerSocketFactory;

import com.major.yodaserver.interrupter.ServerInterrupter;
import com.major.yodaserver.requestprocessor.factory.RequestProcessorFactory;

public class ServerSettings {
    private final ServerSocketFactory serverSocketFactory;
    private final RequestProcessorFactory requestProcessorFactory;
    private final ServerInterrupter serverInterrupter;

    public ServerSettings(ServerSocketFactory serverSocketFactory,
                          RequestProcessorFactory requestProcessorFactory,
                          ServerInterrupter serverInterrupter) {
        this.serverSocketFactory = serverSocketFactory;
        this.requestProcessorFactory = requestProcessorFactory;
        this.serverInterrupter = serverInterrupter;
    }

    public ServerSocketFactory getServerSocketFactory() {
        return serverSocketFactory;
    }

    public RequestProcessorFactory getRequestProcessorFactory() {
        return requestProcessorFactory;
    }

    public ServerInterrupter getServerInterrupter() {
        return serverInterrupter;
    }
}
