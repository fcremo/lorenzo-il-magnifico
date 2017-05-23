package server;

import server.rmiserver.RMIServer;
import server.socketserver.SocketServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class is responsible for loading configuration and spawning the two server threads
 * todo: read configuration from file/cmdline
 */
public class Server {
    private static final Logger LOGGER = Logger.getLogger("Server");

    private boolean enableSocketServer = true;
    private boolean enableRMIServer = true;

    private int socketServerPort = 8420;
    private int rmiServerPort = 1099;

    private Thread socketServer;
    private RMIServer rmiServer;

    private ArrayList<GameRoom> rooms = new ArrayList<>();

    public void start() {
        if (enableRMIServer) {
            try {
                rmiServer = new RMIServer(rmiServerPort, rooms);
            } catch (RemoteException e) {
                LOGGER.severe("Error while trying to create the RMI server: " + e.getMessage());
            }
        }

        if (enableSocketServer) {
            socketServer = new Thread(new SocketServer(socketServerPort, rooms));
            socketServer.start();
        }
    }
}
