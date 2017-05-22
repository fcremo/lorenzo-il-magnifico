package client.socketclient;

import client.exceptions.NetworkException;
import server.ClientToServerInterface;
import client.exceptions.GameNotStartedException;
import client.exceptions.LoginException;
import client.exceptions.NoAvailableRoomsException;
import gamecontroller.GameEventsInterface;
import model.player.Player;
import protocol.SocketProtocol;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is the implementation of the socket based client
 */
public class SocketClient implements ClientToServerInterface {
    private Socket connection;
    private OutputStream outputStream;
    private InputStream inputStream;

    private GameEventsInterface clientController;

    public SocketClient(String server, int port, GameEventsInterface clientController) throws NetworkException {
        try {
            connection = new Socket(server, port);
            outputStream = connection.getOutputStream();
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            throw new NetworkException();
        }

        this.clientController = clientController;
    }

    public void loginPlayer(String name) throws LoginException, NetworkException {
        try {
            outputStream.write(SocketProtocol.loginPlayer(name));
        } catch (IOException e) {
            throw new NetworkException();
        }
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
