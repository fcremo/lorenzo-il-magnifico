package client;

import gamecontroller.GameEventsInterface;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface specifies the server to client methods
 */
public interface ServerToClientInterface extends GameEventsInterface, Remote {
    void pingClient() throws RemoteException;

    void abortGame(String errorMessage) throws RemoteException;

    void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException;

    void askToChooseLeaderCard(List<LeaderCard> leaderCards) throws RemoteException;

    void showWaitingMessage(String message) throws RemoteException;
}
