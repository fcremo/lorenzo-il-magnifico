package server;

import client.exceptions.GameNotStartedException;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.player.Player;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class is responsible for receiving actions from the client connections (regardless of them being socket or RMI)
 * and passing them to the game controller to update the state of the game.
 * It receives events from the game controller and informs all the players in the game of the changes.
 */
public class ServerController implements ClientToServerInterface {
    private static final Logger LOGGER = Logger.getLogger("ServerController");

    /**
     * The connection to the player
     */
    private ClientConnection clientConnection;
    /**
     * The list of rooms
     */
    private ArrayList<GameRoom> gameRooms;
    /**
     * The room the player is in
     */
    private GameRoom chosenRoom;
    /**
     * The connected player.
     * A reference is kept here so that actions received from the player connection
     * can be forwarded to the game connection with the correct "originating" player and
     * avoid the risk of action spoofing.
     */
    private Player player;

    private String username;

    public ServerController(ClientConnection clientConnection, ArrayList<GameRoom> gameRooms) {
        this.clientConnection = clientConnection;
        this.gameRooms = gameRooms;
        clientConnection.setController(this);
    }

    /**
     * Login the player
     * @param name the username chosen by the user
     * @throws LoginException thrown if the name provided is already used or invalid
     * @throws NetworkException
     */
    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException {
        LOGGER.fine("Player " + name + " is logging in");
        username = name;
    }

    /**
     * Join the first available room
     * @throws NoAvailableRoomsException thrown if there are no rooms available
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException {
        LOGGER.fine("Player " + player.getUsername() + " is trying to join a room");
        for (GameRoom room : gameRooms) {
            if (room.isAvailable()) {
                room.addPlayer(this);
                chosenRoom = room;
                return;
            }
        }
        throw new NoAvailableRoomsException();
    }

    /**
     * Create a room and join it
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void createAndJoinRoom() throws NetworkException, RemoteException {
        LOGGER.fine("Player " + player.getUsername() + " is trying to create a room");
        chosenRoom = new GameRoom(player.getUsername() + "'s room");
        chosenRoom.addPlayer(this);
        gameRooms.add(chosenRoom);
    }

    public void onRoomTimeout() {

    }


    @Override
    public ArrayList<Player> getPlayers() throws NetworkException, GameNotStartedException, RemoteException {
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }
}
