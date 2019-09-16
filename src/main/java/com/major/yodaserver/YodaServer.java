package com.major.yodaserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YodaServer {
    private static final Logger logger = LoggerFactory.getLogger(YodaServer.class);

    private static final int DEFAULT_PORT = 80;
    protected static final int LOWEST_AVAILABLE_PORT = 1;
    protected static final int HIGHEST_AVAILABLE_PORT = 65535;

    private final int port;

    public YodaServer() {
        this(DEFAULT_PORT);
    }

    public YodaServer(int port) {
        validatePort(port);
        this.port = port;
    }

    public void listen() {
        logger.info("Running on port: {}", port);
    }

    private void validatePort(int port) {
        if (port < LOWEST_AVAILABLE_PORT || port > HIGHEST_AVAILABLE_PORT) {
            logger.error("Invalid port number: {} (lowest allowed: {}, highest allowed: {})",
                   port, LOWEST_AVAILABLE_PORT, HIGHEST_AVAILABLE_PORT);
            throw new IllegalArgumentException("Port '" + port + "' is not valid");
        }
    }
}
