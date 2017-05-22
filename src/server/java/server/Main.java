package server;

import java.util.logging.Logger;

/**
 * This is the class responsible for launching the server
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger("Server Launcher");

    public static void main(String[] args){
        LOGGER.info("Starting Lorenzo il Magnifico Server..");

        Server s = new Server();
        s.start();
    }

    /**
     * This class is not designed to be instantiated
     */
    private Main(){}
}