package client.rmiclient;

import client.ServerToClientInterface;
import client.exceptions.GameNotStartedException;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import gamecontroller.GameEventsInterface;
import model.Game;
import model.player.Player;
import model.player.bonustile.PersonalBonusTile;
import server.ClientToServerInterface;
import server.rmiserver.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIClient implements ClientToServerInterface, ServerToClientInterface, Remote {
    private GameEventsInterface clientController;

    private ClientToServerInterface connection;


    public RMIClient(String serverHostname, int serverPort, GameEventsInterface clientController) throws RemoteException, NetworkException {
        this.clientController = clientController;

        Registry registry = LocateRegistry.getRegistry(serverHostname, serverPort);

        try {
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            System.out.println("RMIClient already exported");
        }

        try {
            ServerInterface server = (ServerInterface) registry.lookup("LORENZO_SERVER");
            connection = server.getServerConnection(this);
        } catch (NotBoundException e) {
            System.err.println("Server not found (remote object lookup failed)");
        }
    }

    @Override
    public void loginPlayer(String name) throws LoginException, NetworkException, RemoteException {
        connection.loginPlayer(name);
    }

    @Override
    public void joinFirstAvailableRoom() throws NoAvailableRoomsException, NetworkException, RemoteException {
        connection.joinFirstAvailableRoom();

    }

    @Override
    public void createAndJoinRoom() throws NetworkException, RemoteException {
        connection.createAndJoinRoom();
    }

    @Override
    public ArrayList<Player> getPlayers() throws NetworkException, GameNotStartedException {
        return null;
    }

    @Override
    public void pingClient() throws RemoteException {
        System.out.println("Client pinged by the server");
    }

    @Override
    public void setConfiguration(Game game) {

    }

    @Override
    public PersonalBonusTile choosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException {
        clientController.choosePersonalBonusTile(personalBonusTiles);
    }
}
