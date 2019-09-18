package com.major.yodaserver;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.major.yodaserver.interrupter.ServerInterrupter;
import com.major.yodaserver.requestprocessor.factory.RequestProcessorFactory;

public class YodaServer {
    private static final Logger logger = LoggerFactory.getLogger(YodaServer.class);

    private static final int DEFAULT_PORT = 80;
    protected static final int LOWEST_AVAILABLE_PORT = 1;
    protected static final int HIGHEST_AVAILABLE_PORT = 65535;

    private final RequestProcessorFactory requestProcessorFactory;
    private final ServerSocketFactory serverSocketFactory;
    private final ServerInterrupter interrupter;
    private final int port;

    public YodaServer(ServerSettings serverSettings) {
        this(serverSettings, DEFAULT_PORT);
    }

    public YodaServer(ServerSettings serverSettings, int port) {
        validatePort(port);
        this.requestProcessorFactory = serverSettings.getRequestProcessorFactory();
        this.serverSocketFactory = serverSettings.getServerSocketFactory();
        this.interrupter = serverSettings.getServerInterrupter();
        this.port = port;
    }

    public void listen() {
        logger.info("Running on port: {}", port);
        try (ServerSocket server = serverSocketFactory.createServerSocket(port)) {
            while (!interrupter.activated()) {
                server.accept();
            }
        } catch (IOException e) {
            logger.error("Could not create the server socket on port " + port, e);
            throw new RuntimeException("Server startup failure");
        }
    }

    private void validatePort(int port) {
        if (port < LOWEST_AVAILABLE_PORT || port > HIGHEST_AVAILABLE_PORT) {
            logger.error("Invalid port number: {} (lowest allowed: {}, highest allowed: {})",
                   port, LOWEST_AVAILABLE_PORT, HIGHEST_AVAILABLE_PORT);
            throw new IllegalArgumentException("Port '" + port + "' is not valid");
        }
    }
}
