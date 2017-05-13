package it.polimi.ingsw.lorenzo.client;

import it.polimi.ingsw.lorenzo.client.exceptions.GameNotStartedException;
import it.polimi.ingsw.lorenzo.client.exceptions.LoginException;
import it.polimi.ingsw.lorenzo.client.exceptions.NetworkException;
import it.polimi.ingsw.lorenzo.client.exceptions.NoAvailableRoomsException;
import it.polimi.ingsw.lorenzo.model.player.Player;

import java.util.ArrayList;

/**
 * This interface specifies all the requests from client to server.
 * All the actions the player can perform are represented by a method of this interface.
 */
public interface ClientActionsInterface {
    /**
     * Logs in the player
     * @param name the username chosen by the user
     * @throws LoginException thrown if the username is already used
     * @throws NetworkException thrown if there's a network error
     */
    void loginPlayer(String name) throws LoginException, NetworkException;

    /**
     * Joins the first available room
     * @throws NoAvailableRoomsException thrown if there are no rooms available
     * @throws NetworkException thrown if there's a network error
     */
    void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException;

    /**
     * Creates a new game room and joins it
     * @throws NetworkException thrown if there's a network error
     */
    void createAndJoinRoom() throws NetworkException;

    /**
     * Asks the server to initialize the game (load cards, board & other configuration)
     */
    void initializeGame(); // TODO: 5/8/17 server exceptions

    /**
     * Retrieves the list of players from the  server
     */
    ArrayList<Player> getPlayers() throws NetworkException, GameNotStartedException;

}
