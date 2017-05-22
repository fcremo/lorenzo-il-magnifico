package server.rmiserver;

import client.ServerToClientInterface;
import client.exceptions.GameNotStartedException;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.player.Player;
import server.ClientConnection;
import server.ClientToServerInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIClientConnection extends ClientConnection implements ClientToServerInterface, Remote {
    private ServerToClientInterface client;

    public RMIClientConnection(ServerToClientInterface client) {
        this.client = client;
        try{
            UnicastRemoteObject.exportObject(this, 0);
        }
        catch (RemoteException e){
            System.out.println("RMIClientConnection already exported");
        }
    }

    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException, RemoteException {
        System.out.println("rmi loginPlayer");
    }

    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException {
        getController().joinFirstAvailableRoom();
    }

    @Override
    public void createAndJoinRoom() throws NetworkException, RemoteException {

    }

    @Override
    public void initializeGame() throws RemoteException {

    }

    @Override
    public ArrayList<Player> getPlayers() throws RemoteException, NetworkException, GameNotStartedException {
        return null;
    }

    @Override
    public void pingClient() throws RemoteException {

    }
}
