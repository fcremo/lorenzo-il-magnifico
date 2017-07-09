package server;

import client.exceptions.NetworkException;
import gamecontroller.exceptions.ActionNotAllowedException;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.LoginException;
import server.exceptions.NoAvailableGamesException;
import server.exceptions.PersonalBonusTileNotAvailableException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

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
     * Joins the first available game
     *
     * @throws NoAvailableGamesException thrown if there are no games available
     * @throws NetworkException          thrown if there's a network error
     */
    void joinFirstAvailableGame() throws NoAvailableGamesException, NetworkException, RemoteException;

    /**
     * Creates a new game and joins it
     *
     * @throws NetworkException thrown if there's a network error
     */
    void createAndJoinGame() throws NetworkException, RemoteException;

    /**
     * Chooses a personal bonus tile
     *
     * @param personalBonusTile
     * @throws NetworkException
     * @throws RemoteException
     */
    void choosePersonalBonusTile(UUID personalBonusTileId) throws NetworkException, RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException;

    /**
     * Chooses a leader card
     *
     * @param leaderCard
     * @throws NetworkException
     * @throws RemoteException
     */
    void chooseLeaderCard(UUID leaderCardId) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException;

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
     * Tell the server the player wants to go to an action space
     *
     * @param actionSpaceId
     * @param familyMemberColor
     * @param chosenPrivileges
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void goToActionSpace(UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws NetworkException, RemoteException, ActionNotAllowedException;

    /**
     * Tell the server the player wants to go to a floor
     *
     * @param floorId
     * @param familyMemberColor
     * @param councilPrivileges
     * @param paymentForCard
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void goToFloor(UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges, RequiredResourceSet paymentForCard) throws NetworkException, RemoteException, ActionNotAllowedException;

    void discardLeaderCard(UUID leaderCardId, ObtainableResourceSet councilPrivilege) throws NetworkException, RemoteException, ActionNotAllowedException;

    void playLeaderCard(UUID leaderCardId) throws NetworkException, RemoteException, ActionNotAllowedException;

    void endTurn() throws NetworkException, RemoteException, ActionNotAllowedException ;

    // void activateOncePerRoundEffect(Card card, OncePerRoundEffectInterface effect) throws NetworkException, RemoteException, ActionNotAllowedException;
}
