package server;

import client.ServerToClientInterface;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.player.Player;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.exceptions.ActionNotAllowedException;
import server.exceptions.RoomNotJoinableException;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class provides an abstraction of a client connection.
 * It's meant to be extended by other classes implementing a network protocol.
 */
public abstract class ClientConnection implements ServerToClientInterface, ClientToServerInterface {
    private static final Logger LOGGER = Logger.getLogger("ClientConnection");

    /**
     * The connected player.
     * A reference is kept here so that actions received from the player connection
     * can be forwarded to the game connection with the correct "originating" player and
     * prevent action spoofing.
     */
    private Player player;

    /**
     * The player's username, used when the Player object is not yet ready
     */
    private String username;

    /**
     * The game the player is playing
     */
    private ServerGameController serverGameController;

    /**
     * The list of games being played.
     * Needed when the player connects for joining (or creating) one
     */
    private List<ServerGameController> gameRooms;

    public ClientConnection(List<ServerGameController> gameRooms) {
        LOGGER.fine("New player connection!");
        this.gameRooms = gameRooms;
    }

    /**
     * Login the player
     *
     * @param name the username chosen by the user
     * @throws LoginException   thrown if the name provided is already used or invalid
     * @throws NetworkException
     */
    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException, RemoteException {
        // TODO: check if the same username is used on the server
        LOGGER.fine(String.format("Player %s is logging in", name));
        username = name;
    }

    /**
     * Join the first available room
     *
     * @throws NoAvailableRoomsException thrown if there are no rooms available
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException {
        LOGGER.fine(String.format("Player %s is trying to join a room", username));
        for (ServerGameController room : gameRooms) {
            if (room.isJoinable()) {
                try {
                    room.addPlayer(this);
                    this.serverGameController = room;
                }
                catch (RoomNotJoinableException e) {
                    LOGGER.warning("Probable race condition, failed to join a joinable room");
                    // Don't do anything, just go to the next room
                }
                return;
            }
        }
        throw new NoAvailableRoomsException();
    }

    /**
     * Create a room and join it
     *
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void createAndJoinRoom() throws NetworkException, RemoteException {
        LOGGER.fine(String.format("Player %s is trying to create a room", username));
        serverGameController = new ServerGameController();
        serverGameController.addPlayer(this);
        gameRooms.add(serverGameController);
    }

    /**
     * Choose a personal bonus tile
     *
     * @param personalBonusTile
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void choosePersonalBonusTile(PersonalBonusTile personalBonusTile) throws NetworkException, RemoteException, ActionNotAllowedException {
        serverGameController.setPersonalBonusTile(player, personalBonusTile);
    }

    /**
     * Choose a leader card
     *
     * @param leaderCard
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    @Override
    public void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        serverGameController.addLeaderCard(player, leaderCard);
    }

    @Override
    public void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException {
        // TODO
    }

    /**
     * Called when the player wants to go to the council palace
     * @param familyMemberColor
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    @Override
    public void goToCouncilPalace(FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws NetworkException, RemoteException, ActionNotAllowedException {
        serverGameController.goToCouncilPalace(player, familyMemberColor, chosenPrivileges);
    }

    @Override
    public void goToMarket(FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void goToFloor(Floor floor, FamilyMemberColor familyMember, RequiredResourceSet paymentForCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        serverGameController.goToFloor(player, floor, familyMember, paymentForCard);
    }

    @Override
    public void goToSmallHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void goToBigHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void goToSmallProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void goToBigProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void discardLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void playLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getUsername() {
        return username;
    }
}
