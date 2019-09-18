package com.major.yodaserver;

import javax.net.ServerSocketFactory;

public class Yoda {

    public static void main(String[] args) {
        YodaServer yodaServer;
        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            yodaServer = new YodaServer(new ServerSettings(ServerSocketFactory.getDefault(), null, () -> true), port);
        } else {
            yodaServer = new YodaServer(new ServerSettings(ServerSocketFactory.getDefault(), null, () -> true));
        }
        yodaServer.listen();
    }

}
