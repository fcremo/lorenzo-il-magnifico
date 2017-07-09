package client.rmiclient;

import client.ClientController;
import client.ServerToClientInterface;
import client.exceptions.NetworkException;
import gamecontroller.exceptions.ActionNotAllowedException;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.ClientToServerInterface;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.LoginException;
import server.exceptions.NoAvailableGamesException;
import server.exceptions.PersonalBonusTileNotAvailableException;
import server.rmiserver.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

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
    public void joinFirstAvailableGame() throws NoAvailableGamesException, NetworkException, RemoteException {
        connection.joinFirstAvailableGame();

    }

    @Override
    public void createAndJoinGame() throws NetworkException, RemoteException {
        connection.createAndJoinGame();
    }

    @Override
    public void choosePersonalBonusTile(UUID personalBonusTileId) throws NetworkException, RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException {
        connection.choosePersonalBonusTile(personalBonusTileId);
    }

    @Override
    public void chooseLeaderCard(UUID leaderCardId) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException {
        connection.chooseLeaderCard(leaderCardId);
    }

    @Override
    public void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException {
        connection.spendServants(servants);
    }

    @Override
    public void goToActionSpace(ActionSpace actionSpace, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws NetworkException, RemoteException, ActionNotAllowedException {
        connection.goToActionSpace(actionSpace, familyMemberColor, chosenPrivileges);
    }

    @Override
    public void goToFloor(UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges, RequiredResourceSet paymentForCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        connection.goToFloor(floorId, familyMemberColor, councilPrivileges, paymentForCard);
    }

    @Override
    public void discardLeaderCard(UUID leaderCardId, ObtainableResourceSet councilPrivilege) throws NetworkException, RemoteException, ActionNotAllowedException {
        connection.discardLeaderCard(leaderCardId, councilPrivilege);
    }

    @Override
    public void playLeaderCard(UUID leaderCardId) throws NetworkException, RemoteException, ActionNotAllowedException {
        connection.playLeaderCard(leaderCardId);
    }

    @Override
    public void endTurn() throws NetworkException, ActionNotAllowedException, RemoteException {
        connection.endTurn();
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
    public void setGameConfiguration(Game game) throws RemoteException {
        clientController.onSetGameConfiguration(game);
    }

    @Override
    public void onPrepareNewRound() throws RemoteException {
        clientController.onPrepareNewRound();
    }

    @Override
    public void onPlayerTurnStarted(String username) throws RemoteException {
        clientController.onPlayerTurnStarted(username);
    }

    @Override
    public void onCardsDrawn(List<UUID> territoryCards, List<UUID> characterCards, List<UUID> buildingCards, List<UUID> ventureCards) throws RemoteException {
        clientController.onCardsDrawn(territoryCards, characterCards, buildingCards, ventureCards);
    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {
        clientController.onDiceThrown(blackDie, whiteDie, orangeDie);
    }

    @Override
    public void onPlayerSpendsServants(String username, int servants) throws RemoteException {
        clientController.onPlayerSpendsServants(username, servants);
    }

    @Override
    public void onPlayerOccupiesActionSpace(String username, ActionSpace actionSpace, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        clientController.onPlayerOccupiesActionSpace(username, actionSpace, familyMemberColor, councilPrivileges);
    }

    @Override
    public void onPlayerOccupiesFloor(String username, UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges, RequiredResourceSet paymentForCard) throws RemoteException {
        clientController.onPlayerOccupiesFloor(username, floorId, familyMemberColor, chosenPrivileges, paymentForCard);
    }
}
