package server.rmiserver;

import client.ServerToClientInterface;
import server.ClientToServerInterface;
import server.GameRoom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class RMIServer extends UnicastRemoteObject implements ServerInterface {
    private static final Logger LOGGER = Logger.getLogger("RMIServer");

    private static Registry registry;

    private boolean createLocalRegistry = true;
    private int REGISTRY_PORT = 1099;

    private final String NAME = "LORENZO_SERVER";
    private int port;
    private ArrayList<GameRoom> rooms;

    public RMIServer(int port, ArrayList<GameRoom> rooms) throws RemoteException {
        this.port = port;
        this.rooms = rooms;

        System.setProperty("java.rmi.server.useCodebaseOnly", "false");

        if(createLocalRegistry){
            registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            // No need to export objects if the registry is
            // created by the same JVM where the server runs
        }
        else {
            registry = LocateRegistry.getRegistry();
            try {
                UnicastRemoteObject.exportObject(this, 0);
            } catch (RemoteException e) {
                LOGGER.fine("RMIServer object already exported");
            }
        }

        registry.rebind(NAME, this);
    }

    @Override
    public ClientToServerInterface getServerConnection(ServerToClientInterface client) throws RemoteException {
        return new RMIClientConnection(rooms, client);
    }
}
