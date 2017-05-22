package server;

import client.exceptions.GameNotStartedException;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import model.player.Player;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class is responsible for receiving actions from the client connections (regardless of them being socket or RMI)
 * and passing them to the game controller to update the state of the game.
 * It receives events from the game controller and informs all the players in the game of the changes.
 */
public class ServerController implements ClientToServerInterface {
    /**
     * The connection to the player
     */
    private ClientConnection clientConnection;
    private ArrayList<GameRoom> gameRooms;
    private GameRoom chosenRoom;
    private Player player;

    public ServerController(ClientConnection clientConnection, ArrayList<GameRoom> gameRooms) {
        this.clientConnection = clientConnection;
        this.gameRooms = gameRooms;
        clientConnection.setController(this);
    }

    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException {

    }

    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException {

    }

    @Override
    public void createAndJoinRoom() throws NetworkException, RemoteException {

    }

    @Override
    public void initializeGame() throws RemoteException {

    }

    @Override
    public ArrayList<Player> getPlayers() throws NetworkException, GameNotStartedException, RemoteException {
        return null;
    }
}
