package client.rmiclient;

import client.ClientController;
import gamecontroller.exceptions.*;
import model.Game;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import network.ClientToServerInterface;
import network.RMIServerInterface;
import network.ServerToClientInterface;

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

    public RMIClient(String serverHostname, int serverPort, ClientController clientController) throws RemoteException {
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
    public void loginPlayer(String name) throws LoginException, RemoteException {
        connection.loginPlayer(name);
    }

    @Override
    public void joinFirstAvailableGame() throws NoAvailableGamesException, RemoteException {
        connection.joinFirstAvailableGame();

    }

    @Override
    public void createAndJoinGame() throws RemoteException {
        connection.createAndJoinGame();
    }

    @Override
    public void choosePersonalBonusTile(UUID personalBonusTileId) throws RemoteException, PersonalBonusTileNotAvailableException, ActionNotAllowedException {
        connection.choosePersonalBonusTile(personalBonusTileId);
    }

    @Override
    public void chooseLeaderCard(UUID leaderCardId) throws RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException {
        connection.chooseLeaderCard(leaderCardId);
    }

    @Override
    public void spendServants(int servants) throws RemoteException, ActionNotAllowedException {
        connection.spendServants(servants);
    }

    @Override
    public void goToActionSpace(UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws RemoteException, ActionNotAllowedException {
        connection.goToActionSpace(actionSpaceId, familyMemberColor, chosenPrivileges);
    }

    @Override
    public void goToFloor(UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges, RequiredResourceSet paymentForCard) throws RemoteException, ActionNotAllowedException {
        connection.goToFloor(floorId, familyMemberColor, councilPrivileges, paymentForCard);
    }

    @Override
    public void takeDevelopmentCard(UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws ActionNotAllowedException, RemoteException {
        connection.takeDevelopmentCard(cardId, councilPrivileges);
    }

    @Override
    public void discardLeaderCard(UUID leaderCardId, ObtainableResourceSet councilPrivilege) throws RemoteException, ActionNotAllowedException {
        connection.discardLeaderCard(leaderCardId, councilPrivilege);
    }

    @Override
    public void playLeaderCard(UUID leaderCardId) throws RemoteException, ActionNotAllowedException {
        connection.playLeaderCard(leaderCardId);
    }

    @Override
    public void endTurn() throws ActionNotAllowedException, RemoteException {
        connection.endTurn();
    }

    @Override
    public void decideExcommunication(Boolean beExcommunicated) throws RemoteException, ActionNotAllowedException {
        connection.decideExcommunication(beExcommunicated);
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
    public void askWhichImmediateResourcesToTake(UUID cardId) throws RemoteException {
        clientController.showChooseImmediateCouncilPrivileges(cardId);
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
    public void onPlayerOccupiesActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        clientController.onPlayerOccupiesActionSpace(username, actionSpaceId, familyMemberColor, councilPrivileges);
    }

    @Override
    public void onPlayerOccupiesFloor(String username, UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges, RequiredResourceSet paymentForCard) throws RemoteException {
        clientController.onPlayerOccupiesFloor(username, floorId, familyMemberColor, chosenPrivileges, paymentForCard);
    }

    @Override
    public void onPlayerTakesDevelopmentCard(String username, UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        clientController.onPlayerTakesDevelopmentCard(username, cardId, councilPrivileges);
    }

    @Override
    public void onStartVaticanReport() throws RemoteException {
        clientController.onStartVaticanReport();
    }

    @Override
    public void onPlayerDecidesExcommunication(String username, Boolean beExcommunicated) throws RemoteException {
        clientController.onPlayerDecidesExcommunication(username, beExcommunicated);
    }

    @Override
    public void onGameEnd() throws RemoteException {
        clientController.onGameEnd();
    }
}
