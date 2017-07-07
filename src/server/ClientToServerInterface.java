package server;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.exceptions.ActionNotAllowedException;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.PersonalBonusTileNotAvailableException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

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
     * Tell the servants the player wants to go to the council palace
     *
     * @param familyMemberColor
     * @param chosenPrivileges
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    void goToCouncilPalace(FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws NetworkException, RemoteException, ActionNotAllowedException;

    void goToMarket(FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) throws NetworkException, RemoteException, ActionNotAllowedException;

    void goToFloor(Floor floor, FamilyMemberColor familyMember, RequiredResourceSet paymentForCard) throws NetworkException, RemoteException, ActionNotAllowedException;

    void goToSmallHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

    void goToBigHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

    void goToSmallProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

    void goToBigProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

    void discardLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException;

    void playLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException;

    // void activateOncePerRoundEffect(Card card, OncePerRoundEffectInterface effect) throws NetworkException, RemoteException, ActionNotAllowedException;
}
