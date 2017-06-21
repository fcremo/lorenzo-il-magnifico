package server.rmiserver;

import client.ServerToClientInterface;
import gamecontroller.GameState;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import server.ClientConnection;
import server.ClientToServerInterface;
import server.GameRoom;

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

    public RMIClientConnection(ArrayList<GameRoom> gameRooms, ServerToClientInterface client) {
        super(gameRooms);
        this.client = client;
        try {
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            System.out.println("RMIClientConnection already exported");
        }
    }

    @Override
    public void onGameStateChange(GameState gameState) throws RemoteException {
        client.onGameStateChange(gameState);
    }

    @Override
    public void pingClient() throws RemoteException {
        client.pingClient();
    }

    @Override
    public void abortGame(String errorMessage) throws RemoteException {
        client.abortGame(errorMessage);
    }

    @Override
    public void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException {
        client.askToChoosePersonalBonusTile(personalBonusTiles);
    }

    @Override
    public void askToChooseLeaderCard(List<LeaderCard> leaderCards) throws RemoteException {
        client.askToChooseLeaderCard(leaderCards);
    }

    @Override
    public void showWaitingMessage(String message) throws RemoteException {
        client.showWaitingMessage(message);
    }
}
