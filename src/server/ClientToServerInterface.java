package server;

import gamecontroller.exceptions.ActionNotAllowedException;
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
     */
    void loginPlayer(String name) throws LoginException, RemoteException;

    /**
     * Joins the first available game
     *
     * @throws NoAvailableGamesException thrown if there are no games available
     */
    void joinFirstAvailableGame() throws NoAvailableGamesException, RemoteException;

    /**
     * Creates a new game and joins it
     *
     */
    void createAndJoinGame() throws RemoteException;

    /**
     * Chooses a personal bonus tile
     *
     * @param personalBonusTile
     * @throws RemoteException
     */
    void choosePersonalBonusTile(UUID personalBonusTileId) throws RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException;

    /**
     * Chooses a leader card
     *
     * @param leaderCard
     * @throws RemoteException
     */
    void chooseLeaderCard(UUID leaderCardId) throws RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException;

    /**
     * Tell the server how many servants the player wants to spend for the next action
     *
     * @param servants
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void spendServants(int servants) throws RemoteException, ActionNotAllowedException;

    /**
     * Tell the server the player wants to go to an action space
     *
     * @param actionSpaceId
     * @param familyMemberColor
     * @param chosenPrivileges
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void goToActionSpace(UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws RemoteException, ActionNotAllowedException;

    /**
     * Tell the server the player wants to go to a floor
     *
     * @param floorId
     * @param familyMemberColor
     * @param councilPrivileges
     * @param paymentForCard
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void goToFloor(UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges, RequiredResourceSet paymentForCard) throws RemoteException, ActionNotAllowedException;

    void takeDevelopmentCard(UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws RemoteException, ActionNotAllowedException;

    void discardLeaderCard(UUID leaderCardId, ObtainableResourceSet councilPrivilege) throws RemoteException, ActionNotAllowedException;

    void playLeaderCard(UUID leaderCardId) throws RemoteException, ActionNotAllowedException;

    void endTurn() throws RemoteException, ActionNotAllowedException ;

    void decideExcommunication(Boolean beExcommunicated) throws RemoteException, ActionNotAllowedException;

    // void activateOncePerRoundEffect(Card card, OncePerRoundEffectInterface effect) throws RemoteException, ActionNotAllowedException;
}
