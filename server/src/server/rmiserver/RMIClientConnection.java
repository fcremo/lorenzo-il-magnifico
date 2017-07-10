package server.rmiserver;

import network.ServerToClientInterface;
import model.Game;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.ClientConnection;
import network.ClientToServerInterface;
import server.ServerGameController;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class implements the ClientConnection class for RMI.
 * Clients will get a stub that will call methods on a server side instance of this class.
 *
 * The server can use this object to make calls on the clients using the ServerToClientInterface.
 */
public class RMIClientConnection extends ClientConnection implements ServerToClientInterface, ClientToServerInterface, Remote {
    private static final Logger LOGGER = Logger.getLogger("RMIClientConnection");

    private ServerToClientInterface client;

    @SuppressWarnings("squid:S1166") // Silence "Log or rethrow this exception", there's no problem if the object is already exported
    public RMIClientConnection(List<ServerGameController> games, ServerToClientInterface client) {
        super(games);
        this.client = client;
        try {
            UnicastRemoteObject.exportObject(this, 0);
        }
        catch (RemoteException e) {
            LOGGER.fine("RMIClientConnection already exported");
        }
    }

    /* ----------------------------------------------------------------------
     * Server to client interface
     * ---------------------------------------------------------------------- */

    @Override
    public void pingClient() throws RemoteException {
        client.pingClient();
    }

    @Override
    public void abortGame(String errorMessage) throws RemoteException {
        client.abortGame(errorMessage);
    }

    @Override
    public void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException {
        client.askToChoosePersonalBonusTile(personalBonusTiles);
    }

    @Override
    public void askToChooseLeaderCard(List<LeaderCard> leaderCards) throws RemoteException {
        client.askToChooseLeaderCard(leaderCards);
    }

    @Override
    public void askWhichImmediateResourcesToTake(UUID cardId) throws RemoteException {
        client.askWhichImmediateResourcesToTake(cardId);
    }

    @Override
    public void showWaitingMessage(String message) throws RemoteException {
        client.showWaitingMessage(message);
    }

    @Override
    public void setGameConfiguration(Game game) throws RemoteException {
        client.setGameConfiguration(game);
    }

    /* ----------------------------------------------------------------------
     * Game events interface
     * ---------------------------------------------------------------------- */
    @Override
    public void onPrepareNewRound() throws RemoteException {
        client.onPrepareNewRound();
    }

    @Override
    public void onPlayerTurnStarted(String username) throws RemoteException {
        client.onPlayerTurnStarted(username);
    }

    @Override
    public void onCardsDrawn(List<UUID> territoryCards, List<UUID> characterCards, List<UUID> buildingCards, List<UUID> ventureCards) throws RemoteException {
        client.onCardsDrawn(territoryCards, characterCards, buildingCards, ventureCards);
    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {
        client.onDiceThrown(blackDie, whiteDie, orangeDie);
    }

    @Override
    public void onPlayerOccupiesActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        client.onPlayerOccupiesActionSpace(username, actionSpaceId, familyMemberColor, councilPrivileges);
    }

    @Override
    public void onPlayerOccupiesFloor(String username, UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges, RequiredResourceSet paymentForCard) throws RemoteException {
        client.onPlayerOccupiesFloor(username, floorId, familyMemberColor, chosenPrivileges, paymentForCard);
    }

    @Override
    public void onPlayerSpendsServants(String username, int servants) throws RemoteException {
        client.onPlayerSpendsServants(username, servants);
    }

    @Override
    public void onPlayerTakesDevelopmentCard(String username, UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        client.onPlayerTakesDevelopmentCard(username, cardId, councilPrivileges);
    }

    @Override
    public void onStartVaticanReport() throws RemoteException {
        client.onStartVaticanReport();
    }

    @Override
    public void onPlayerDecidesExcommunication(String username, Boolean beExcommunicated) throws RemoteException {
        client.onPlayerDecidesExcommunication(username, beExcommunicated);
    }

    @Override
    public void onGameEnd() throws RemoteException {
        client.onGameEnd();
    }
}