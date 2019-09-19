package com.major.yodaserver;

import java.io.File;

public class ServerContext {
    private final File rootDir;
    private Integer port;

    public ServerContext(File rootDir) {
        this.rootDir = rootDir;
    }

    public ServerContext(File rootDir, Integer port) {
        this.rootDir = rootDir;
        this.port = port;
    }

    public File getRootDir() {
        return rootDir;
    }

    public Integer getPort() {
        return port;
    }
}
