package client.rmiclient;

import client.exceptions.GameNotStartedException;
import client.exceptions.NetworkException;
import client.ServerToClientInterface;
import client.exceptions.LoginException;
import client.exceptions.NoAvailableRoomsException;
import gamecontroller.GameEventsInterface;
import model.player.Player;
import server.ClientToServerInterface;
import server.rmiserver.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIClient implements ClientToServerInterface, ServerToClientInterface, Remote {
    private GameEventsInterface clientController;

    private ServerInterface server;
    private ClientToServerInterface connection;


    public RMIClient(String server, int port, GameEventsInterface clientController) throws RemoteException, NetworkException {
        this.clientController = clientController;

        Registry registry = LocateRegistry.getRegistry(server, port);

        try{
            UnicastRemoteObject.exportObject(this, 0);
        }
        catch (RemoteException e){
            System.out.println("RMIClient already exported");
        }

        try {
            this.server = (ServerInterface) registry.lookup("LORENZO_SERVER");
            connection = this.server.getServerConnection(this);
        } catch (NotBoundException e) {
            System.err.println("Server not found (remote object lookup failed)");
        }
    }

    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException, RemoteException {
        connection.loginPlayer(name);
    }

    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException {

    }

    @Override
    public void createAndJoinRoom() throws NetworkException, RemoteException {

    }

    @Override
    public void initializeGame() throws RemoteException {

    }

    @Override
    public ArrayList<Player> getPlayers() throws NetworkException, GameNotStartedException {
        return null;
    }

    @Override
    public void pingClient() throws RemoteException {
        System.out.println("Client pinged by the server");
    }
}