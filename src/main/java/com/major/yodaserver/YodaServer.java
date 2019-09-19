package com.major.yodaserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.major.yodaserver.interrupter.ServerInterrupter;
import com.major.yodaserver.requestprocessor.factory.RequestProcessorFactory;

public class YodaServer {
    private static final Logger logger = LoggerFactory.getLogger(YodaServer.class);

    private static final int NUMBER_OF_THREADS = 20;

    private static final int DEFAULT_PORT = 80;
    protected static final int LOWEST_AVAILABLE_PORT = 1;
    protected static final int HIGHEST_AVAILABLE_PORT = 65535;

    private final RequestProcessorFactory requestProcessorFactory;
    private final ServerSocketFactory serverSocketFactory;
    private final ServerInterrupter interrupter;
    private final int port;
    private final File rootDir;


    public YodaServer(ServerSettings serverSettings, ServerContext context) {
        this.port = Optional.ofNullable(context.getPort()).orElse(DEFAULT_PORT);
        validatePort(port);
        this.rootDir = context.getRootDir();
        this.requestProcessorFactory = serverSettings.getRequestProcessorFactory();
        this.serverSocketFactory = serverSettings.getServerSocketFactory();
        this.interrupter = serverSettings.getServerInterrupter();
    }

    public void listen() {
        logger.info("Running on port: {}", port);
        ExecutorService pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        try (ServerSocket server = serverSocketFactory.createServerSocket(port)) {
            while (!interrupter.activated()) {
                try {
                    Socket connection = server.accept();
                    pool.submit(requestProcessorFactory.createRequestProcessor(rootDir, connection));
                } catch (RuntimeException e) {
                    // just log the exception, do not stop the server because of a failing request
                    logger.error("Could not process a request", e);
                }
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
