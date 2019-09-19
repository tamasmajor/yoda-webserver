package com.major.yodaserver;

import java.io.File;
import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.major.yodaserver.interrupter.EndlessInterrupter;
import com.major.yodaserver.requestprocessor.factory.YodaRequestProcessorFactory;

public class Yoda {
    private static Logger logger = LoggerFactory.getLogger(Yoda.class);

    public static void main(String[] args) {
        YodaServer yodaServer;

        File rootDirectory;
        try {
            rootDirectory = new File(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Providing root directory is mandatory");
            return;
        }

        if (args.length > 1) {
            int port = Integer.parseInt(args[1]);
            yodaServer = new YodaServer(new ServerSettings(ServerSocketFactory.getDefault(),
                                                           new YodaRequestProcessorFactory(),
                                                           new EndlessInterrupter()),
                                        new ServerContext(rootDirectory, port));
        } else {
            yodaServer = new YodaServer(new ServerSettings(ServerSocketFactory.getDefault(),
                                                           new YodaRequestProcessorFactory(),
                                                           new EndlessInterrupter()),
                                        new ServerContext(rootDirectory));
        }
        yodaServer.listen();
    }

}
