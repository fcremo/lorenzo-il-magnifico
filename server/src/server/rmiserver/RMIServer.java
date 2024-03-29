package server.rmiserver;

import network.RMIServerInterface;
import network.ServerToClientInterface;
import server.ServerGameController;
import network.ClientToServerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Logger;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
    private static final Logger LOGGER = Logger.getLogger("RMIServer");

    private static Registry registry;
    private final String NAME = "LORENZO_SERVER";
    private boolean createLocalRegistry = true;
    private int REGISTRY_PORT = 1099;
    private int port;
    private List<ServerGameController> games;

    public RMIServer(int port, List<ServerGameController> games) throws RemoteException {
        this.port = port;
        this.games = games;

        System.setProperty("java.rmi.server.useCodebaseOnly", "false");

        if (createLocalRegistry) {
            registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            // No need to export objects if the registry is
            // created by the same JVM where the server runs
        }
        else {
            registry = LocateRegistry.getRegistry();
            try {
                UnicastRemoteObject.exportObject(this, 0);
            }
            catch (RemoteException e) {
                LOGGER.fine("RMIServer object already exported");
            }
        }

        registry.rebind(NAME, this);
    }

    @Override
    public ClientToServerInterface getServerConnection(ServerToClientInterface client) throws RemoteException {
        return new RMIClientConnection(games, client);
    }
}
