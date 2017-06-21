package server;

import client.ServerToClientInterface;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import gamecontroller.exceptions.ActionNotAllowedException;
import gamecontroller.exceptions.LeaderCardNotAvailableException;
import gamecontroller.exceptions.PersonalBonusTileNotAvailableException;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.player.Player;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This abstract class provides an abstraction of a client connection.
 * It's meant to be extended by other classes implementing a network protocol.
 */
public abstract class ClientConnection implements ServerToClientInterface, ClientToServerInterface {
    private static final Logger LOGGER = Logger.getLogger("Server");

    /**
     * The connected player.
     * A reference is kept here so that actions received from the player connection
     * can be forwarded to the game connection with the correct "originating" player and
     * prevent action spoofing.
     */
    private Player player;

    /**
     * The player's username
     */
    private String username;

    /**
     * The room the player is in
     */
    private GameRoom room;

    /**
     * The list of rooms. Needed when the player connects for joining (or creating) one
     */
    private List<GameRoom> gameRooms;

    public ClientConnection(List<GameRoom> gameRooms) {
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
        LOGGER.fine("Player " + username + " is trying to join a room");
        for (GameRoom room : gameRooms) {
            if (room.isAvailable()) {
                room.addPlayer(this);
                this.room = room;
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
        room = new GameRoom(String.format("%s's room", username));
        room.addPlayer(this);
        gameRooms.add(room);
    }

    /**
     * Choose a personal bonus tile
     *
     * @param personalBonusTile
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void choosePersonalBonusTile(PersonalBonusTile personalBonusTile) throws NetworkException, RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException {
        room.getServerGameController().setPersonalBonusTile(player, personalBonusTile);
    }

    @Override
    public void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException {
        room.getServerGameController().addLeaderCard(player, leaderCard);
    }

    private boolean isThisPlayerTurn() {
        return player.equals(room.getServerGameController().getGame().getCurrentPlayer());
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
