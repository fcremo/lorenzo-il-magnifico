package client.rmiclient;

import client.ClientController;
import client.ServerToClientInterface;
import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import gamecontroller.GameState;
import model.board.actionspace.ActionSpace;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.player.FamilyMemberColor;
import server.exceptions.ActionNotAllowedException;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.PersonalBonusTileNotAvailableException;
import model.Game;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.player.Player;
import server.ClientToServerInterface;
import server.rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * This class implements the ClientToServer and ServerToClient interfaces via RMI
 */
public class RMIClient implements ClientToServerInterface, ServerToClientInterface, Remote {
    private ClientController clientController;

    private ClientToServerInterface connection;

    public RMIClient(String serverHostname, int serverPort, ClientController clientController) throws RemoteException, NetworkException {
        this.clientController = clientController;

        Registry registry = LocateRegistry.getRegistry(serverHostname, serverPort);

        try {
            UnicastRemoteObject.exportObject(this, 0);
        }
        catch (RemoteException e) {
            System.out.println("RMIClient already exported");
        }

        try {
            RMIServerInterface server = (RMIServerInterface) registry.lookup("LORENZO_SERVER");
            connection = server.getServerConnection(this);
        }
        catch (NotBoundException e) {
            System.err.println("Server not found (remote object lookup failed)");
        }
    }

    /* ----------------------------------------------------------
     * CLIENT TO SERVER INTERFACE
     * ---------------------------------------------------------- */
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
    public void choosePersonalBonusTile(PersonalBonusTile personalBonusTile) throws NetworkException, RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException {
        connection.choosePersonalBonusTile(personalBonusTile);
    }

    @Override
    public void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException {
        connection.chooseLeaderCard(leaderCard);
    }

    @Override
    public void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException {
        connection.spendServants(servants);
    }

    @Override
    public void goToActionSpace(ActionSpace actionSpace, FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {
        connection.goToActionSpace(actionSpace, familyMemberColor);
    }

    /* ----------------------------------------------------------
     * SERVER TO CLIENT INTERFACE
     * ---------------------------------------------------------- */
    @Override
    public void pingClient() throws RemoteException {
        System.out.println("Client pinged by the server");
    }

    @Override
    public void abortGame(String errorMessage) throws RemoteException {
        clientController.abortGame(errorMessage);
    }

    @Override
    public void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException {
        clientController.showChoosePersonalBonusTile(personalBonusTiles);
    }

    @Override
    public void askToChooseLeaderCard(List<LeaderCard> leaderCards) throws RemoteException {
        clientController.showChooseLeaderCard(leaderCards);
    }

    @Override
    public void showWaitingMessage(String message) throws RemoteException {
        clientController.showWaitingMessage(message);
    }

    @Override
    public void onGameStateChange(GameState gameState) throws RemoteException {
        clientController.onGameStateChange(gameState);
    }

    @Override
    public void setGameConfiguration(Game game) throws RemoteException {
        clientController.onSetGameConfiguration(game);
    }

    @Override
    public void onTurnOrderChanged(List<Player> playerOrder) throws RemoteException {
        clientController.onTurnOrderChanged(playerOrder);
    }

    @Override
    public void onPlayerTurnStarted(Player player) throws RemoteException {
        clientController.onPlayerTurnStarted(player);
    }

    @Override
    public void onCardsDrawn(List<TerritoryCard> territoryCards, List<CharacterCard> characterCards, List<BuildingCard> buildingCards, List<VentureCard> ventureCards) throws RemoteException {
        clientController.onCardsDrawn(territoryCards, characterCards, buildingCards, ventureCards);
    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {
        clientController.onDiceThrown(blackDie, whiteDie, orangeDie);
    }

    @Override
    public void onPlayerOccupiesActionSpace(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) throws RemoteException {
        clientController.onPlayerOccupiesActionSpace(player, familyMemberColor, actionSpace);
    }
}
