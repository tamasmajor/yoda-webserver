package com.major.yodaserver;

import javax.net.ServerSocketFactory;

import com.major.yodaserver.interrupter.EndlessInterrupter;

public class Yoda {

    public static void main(String[] args) {
        YodaServer yodaServer;
        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            yodaServer = new YodaServer(new ServerSettings(ServerSocketFactory.getDefault(), null, new EndlessInterrupter()), port);
        } else {
            yodaServer = new YodaServer(new ServerSettings(ServerSocketFactory.getDefault(), null, new EndlessInterrupter()));
        }
        yodaServer.listen();
    }

}
