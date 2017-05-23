package server;

import client.exceptions.GameNotStartedException;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.player.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

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
     * Asks the server to initialize the game (load cards, board & other configuration)
     */
    void initializeGame() throws RemoteException;

    /**
     * Retrieves the list of players from the  server
     */
    ArrayList<Player> getPlayers() throws RemoteException, NetworkException, GameNotStartedException;

}
