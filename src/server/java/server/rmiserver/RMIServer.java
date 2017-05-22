package server.rmiserver;

import client.ServerToClientInterface;
import server.ClientToServerInterface;
import server.GameRoom;
import server.ServerController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIServer extends UnicastRemoteObject implements ServerInterface {
    private final String NAME = "LORENZO_SERVER";
    private static Registry registry;

    private int port;
    private ArrayList<GameRoom> rooms;

    public RMIServer(int port, ArrayList<GameRoom> rooms) throws RemoteException {
        this.port = port;
        this.rooms = rooms;
        registry = LocateRegistry.getRegistry();

        try{
            UnicastRemoteObject.exportObject(this, 0);
        }
        catch (RemoteException e){
            System.out.println("RMIServer object already exported");
        }

        registry.rebind(NAME, this);
    }

    @Override
    public ClientToServerInterface getServerConnection(ServerToClientInterface client) throws RemoteException {
        RMIClientConnection connection = new RMIClientConnection(client);
        new ServerController(connection, rooms);
        return connection;
    }
}
