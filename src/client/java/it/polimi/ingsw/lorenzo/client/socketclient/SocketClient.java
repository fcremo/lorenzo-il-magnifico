package it.polimi.ingsw.lorenzo.client.socketclient;

import it.polimi.ingsw.lorenzo.client.ClientActionsInterface;
import it.polimi.ingsw.lorenzo.client.exceptions.GameNotStartedException;
import it.polimi.ingsw.lorenzo.client.exceptions.LoginException;
import it.polimi.ingsw.lorenzo.client.exceptions.NetworkException;
import it.polimi.ingsw.lorenzo.client.exceptions.NoAvailableRoomsException;
import it.polimi.ingsw.lorenzo.gamecontroller.GameEventsInterface;
import it.polimi.ingsw.lorenzo.model.player.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is the implementation of the socket based client
 */
public class SocketClient implements ClientActionsInterface {
    private Socket connection;
    private OutputStream outputStream;
    private InputStream inputStream;

    private GameEventsInterface gameController;

    public SocketClient(String server, int port/*, GameEventsInterface gameController*/) throws NetworkException {
        try {
            connection = new Socket(server, port);
            this.outputStream = connection.getOutputStream();
            this.inputStream = connection.getInputStream();
        } catch (IOException e) {
            throw new NetworkException();
        }

        //this.gameController = gameController;
    }

    public void loginPlayer(String name) throws LoginException, NetworkException {
        throw new NotImplementedException();
        /*try {
            // outputStream.write(SocketProtocol.setPlayerName(name).getBytes());
        } catch (IOException e) {
            throw new NetworkException();
        }*/
    }

    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException {
        throw new NotImplementedException();
        /*try {
            // outputStream.write(SocketProtocol.setPlayerName(name).getBytes());
        } catch (IOException e) {
            throw new NetworkException();
        }*/
    }

    public void createAndJoinRoom() throws NetworkException {

    }

    public void initializeGame() {

    }


    public ArrayList<Player> getPlayers() throws NetworkException, GameNotStartedException {
        throw new NotImplementedException();
    }
}
