package server;

import java.util.logging.Logger;

/**
 * This is the class responsible for launching the server
 */
public class ServerMain {
    private static final Logger LOGGER = Logger.getLogger("Server Launcher");

    /**
     * This class is not designed to be instantiated
     */
    private ServerMain() {
    }

    public static void main(String[] args) {
        LOGGER.info("Starting Lorenzo il Magnifico Server..");

        Server s = new Server();
        s.start();
    }
}