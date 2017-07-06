package client.socketclient;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import gamecontroller.GameEventsInterface;
import model.board.actionspace.ActionSpace;
import model.player.FamilyMemberColor;
import server.exceptions.ActionNotAllowedException;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.PersonalBonusTileNotAvailableException;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import server.socketserver.protocol.SocketProtocol;
import server.ClientToServerInterface;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

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
        }
        catch (IOException e) {
            throw new NetworkException();
        }

        this.clientController = clientController;
    }

    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException {
        try {
            outputStream.write(SocketProtocol.loginPlayer(name));
        }
        catch (IOException e) {
            throw new NetworkException();
        }
    }

    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException {
        throw new NotImplementedException();
        /*try {
            // outputStream.write(SocketProtocol.setPlayerName(name).getBytes());
        } catch (IOException e) {
            throw new NetworkException();
        }*/
    }

    @Override
    public void createAndJoinRoom() throws NetworkException {

    }

    @Override
    public void choosePersonalBonusTile(PersonalBonusTile personalBonusTile) throws NetworkException, RemoteException, PersonalBonusTileNotAvailableException {

    }

    @Override
    public void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException {

    }

    @Override
    public void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void goToActionSpace(ActionSpace actionSpace, FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {

    }
}