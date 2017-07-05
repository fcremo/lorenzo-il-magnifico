package server;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.board.actionspace.ActionSpace;
import model.player.FamilyMemberColor;
import server.exceptions.ActionNotAllowedException;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.PersonalBonusTileNotAvailableException;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface specifies all the requests from client to server.
 * All the actions the player can perform are represented by a method of this interface.
 */
public interface ClientToServerInterface extends Remote {
    /**
     * Logs in the player
     *
     * @param name the username chosen by the user
     * @throws LoginException   thrown if the username is already used
     * @throws NetworkException thrown if there's a network error
     */
    void loginPlayer(String name) throws LoginException, NetworkException, RemoteException;

    /**
     * Joins the first available room
     *
     * @throws NoAvailableRoomsException thrown if there are no rooms available
     * @throws NetworkException          thrown if there's a network error
     */
    void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException;

    /**
     * Creates a new game room and joins it
     *
     * @throws NetworkException thrown if there's a network error
     */
    void createAndJoinRoom() throws NetworkException, RemoteException;

    /**
     * Chooses a personal bonus tile
     *
     * @param personalBonusTile
     * @throws NetworkException
     * @throws RemoteException
     */
    void choosePersonalBonusTile(PersonalBonusTile personalBonusTile) throws NetworkException, RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException;

    /**
     * Chooses a leader card
     *
     * @param leaderCard
     * @throws NetworkException
     * @throws RemoteException
     */
    void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException;

    /**
     * Tell the server how many servants the player wants to spend for the next action
     *
     * @param servants
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException;

    /**
     * Go to an action space
     *
     * @param actionSpace
     * @param familyMemberColor
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void goToActionSpace(ActionSpace actionSpace, FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;
}
