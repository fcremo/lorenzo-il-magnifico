package client;

import gamecontroller.GameEventsInterface;
import model.Game;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.resource.RequiredResourceSet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This interface specifies the server to client methods
 */
public interface ServerToClientInterface extends GameEventsInterface, Remote {
    /**
     * Used to ping the clients periodically to check it's alive and avoid connection loss due to NATs
     *
     * @throws RemoteException
     */
    void pingClient() throws RemoteException;

    /**
     * Called when the server decides the game has to terminate prematurely
     *
     * @param errorMessage an error message describing the error condition
     * @throws RemoteException
     */
    void abortGame(String errorMessage) throws RemoteException;

    /**
     * Called when the player has to choose a personal bonus tile
     *
     * @param personalBonusTiles the possible bonus tiles to choose
     * @throws RemoteException
     */
    void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException;

    /**
     * Called when the player has to choose a leader card during the initial draft
     *
     * @param leaderCards the possible leader cards to choose
     * @throws RemoteException
     */
    void askToChooseLeaderCard(List<LeaderCard> leaderCards) throws RemoteException;

    /**
     * Called when the player has to choose a resource set to pay
     *
     * @param requiredResourceSets
     * @throws RemoteException
     */
    void askToChooseRequiredResourceSet(List<RequiredResourceSet> requiredResourceSets) throws RemoteException;

    /**
     * Called when the player has to wait
     *
     * @param message the reason the player has to wait
     * @throws RemoteException
     */
    void showWaitingMessage(String message) throws RemoteException;

    /**
     * Called when the server sends game configuration to a player
     * @param game
     * @throws RemoteException
     */
    void setGameConfiguration(Game game) throws RemoteException;
}
