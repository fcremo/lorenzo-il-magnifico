package server.rmiserver;

import client.ServerToClientInterface;
import client.exceptions.GameNotStartedException;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.Game;
import model.player.Player;
import model.player.bonustile.PersonalBonusTile;
import server.ClientConnection;
import server.ClientToServerInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the ClientToServerInterface for RMI.
 * Clients will get a stub that will call (via RMI) methods on a server side instance of this class.
 *
 * The server can also use this object to push messages to the clients using the ServerToClientInterface.
 */
public class RMIClientConnection extends ClientConnection implements ServerToClientInterface, ClientToServerInterface, Remote {
    private ServerToClientInterface client;

    public RMIClientConnection(ServerToClientInterface client) {
        this.client = client;
        try {
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            System.out.println("RMIClientConnection already exported");
        }
    }

    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException, RemoteException {
        getController().loginPlayer(name);
    }

    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException {
        getController().joinFirstAvailableRoom();
    }

    @Override
    public void createAndJoinRoom() throws NetworkException, RemoteException {
        getController().createAndJoinRoom();
    }

    @Override
    public ArrayList<Player> getPlayers() throws RemoteException, NetworkException, GameNotStartedException {
        return null;
    }

    @Override
    public void pingClient() throws RemoteException {
        client.pingClient();
    }

    @Override
    public void setConfiguration(Game game) {

    }

    @Override
    public PersonalBonusTile choosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException {
        return client.choosePersonalBonusTile(personalBonusTiles);
    }
}
