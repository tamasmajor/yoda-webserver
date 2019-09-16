package com.major.yodaserver;

public class Yoda {

    public static void main(String[] args) {
        YodaServer yodaServer;
        if (args.length > 0) {
            yodaServer = new YodaServer(Integer.parseInt(args[0]));
        } else {
            yodaServer = new YodaServer();
        }
        yodaServer.listen();
    }

}
