package server;

import client.ServerToClientInterface;
import gamecontroller.exceptions.ActionNotAllowedException;
import model.player.FamilyMemberColor;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.exceptions.GameNotJoinableException;
import server.exceptions.LoginException;
import server.exceptions.NoAvailableGamesException;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class provides an abstraction of a client connection.
 * It's meant to be extended by other classes implementing a network protocol.
 */
public abstract class ClientConnection implements ServerToClientInterface, ClientToServerInterface {
    private static final Logger LOGGER = Logger.getLogger("ClientConnection");

    /**
     * The player's username, used when the Player object is not yet ready
     */
    private String username;

    /**
     * The game the player is playing
     */
    private ServerGameController serverGameController;

    /**
     * The list of games being played.
     * Needed when the player connects for joining (or creating) one
     */
    private List<ServerGameController> gameControllers;

    public ClientConnection(List<ServerGameController> gameControllers) {
        LOGGER.fine("New player connection!");
        this.gameControllers = gameControllers;
    }

    /**
     * Login the player
     *
     * @param username the username chosen by the user
     * @throws LoginException   thrown if the name provided is already used or invalid
     */
    @Override
    public void loginPlayer(String username) throws LoginException, RemoteException {
        LOGGER.fine(String.format("Player %s is logging in", username));

        // check if the same username is already used on the server
        boolean alreadyUsedUsername = gameControllers.stream()
                                                     .flatMap(gameController -> gameController.getConnections().stream())
                                                     .anyMatch(connection -> connection.getUsername().equals(username));

        if(alreadyUsedUsername) throw new LoginException("This username is already taken");

        this.username = username;
    }

    /**
     * Join the first available game
     *
     * @throws NoAvailableGamesException thrown if there are no games available
     * @throws RemoteException
     */
    @Override
    public void joinFirstAvailableGame() throws NoAvailableGamesException, RemoteException {
        LOGGER.fine(String.format("Player %s is trying to join a game", username));
        for (ServerGameController game : gameControllers) {
            if (game.isJoinable()) {
                try {
                    game.addPlayer(this);
                    this.serverGameController = game;
                }
                catch (GameNotJoinableException e) {
                    LOGGER.warning("Probable race condition, failed to join a joinable game");
                    // Don't do anything, just go to the next game
                }
                return;
            }
        }
        throw new NoAvailableGamesException();
    }

    /**
     * Create a game and join it
     *
     * @throws RemoteException
     */
    @Override
    public void createAndJoinGame() throws RemoteException {
        LOGGER.fine(String.format("Player %s is trying to create a new game", username));
        serverGameController = new ServerGameController();
        serverGameController.addPlayer(this);
        gameControllers.add(serverGameController);
    }

    /**
     * Choose a personal bonus tile
     *
     * @param personalBonusTileId
     * @throws RemoteException
     */
    @Override
    public void choosePersonalBonusTile(UUID personalBonusTileId) throws RemoteException, ActionNotAllowedException {
        serverGameController.choosePersonalBonusTile(username, personalBonusTileId);
    }

    /**
     * Choose a leader card
     *
     * @param leaderCardId
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    @Override
    public void chooseLeaderCard(UUID leaderCardId) throws RemoteException, ActionNotAllowedException {
        serverGameController.chooseLeaderCard(username, leaderCardId);
    }

    @Override
    public void spendServants(int servants) throws RemoteException, ActionNotAllowedException {
        serverGameController.spendServants(username, servants);
    }

    /**
     * Called when the player wants to go to an action space
     *
     * @param actionSpaceId
     * @param familyMemberColor
     * @param chosenPrivileges
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    @Override
    public void goToActionSpace(UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws RemoteException, ActionNotAllowedException {
        serverGameController.goToActionSpace(username, actionSpaceId, familyMemberColor, chosenPrivileges);
    }

    @Override
    public void goToFloor(UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges, RequiredResourceSet paymentForCard) throws RemoteException, ActionNotAllowedException {
        serverGameController.goToFloor(username, floorId, familyMemberColor, paymentForCard, councilPrivileges);
    }

    @Override
    public void takeDevelopmentCard(UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws RemoteException, ActionNotAllowedException {
        serverGameController.takeDevelopmentCard(username, cardId, councilPrivileges);
    }

    @Override
    public void discardLeaderCard(UUID leaderCardId, ObtainableResourceSet councilPrivilegeA) throws RemoteException, ActionNotAllowedException {
        // TODO
    }

    @Override
    public void playLeaderCard(UUID leaderCardId) throws RemoteException, ActionNotAllowedException {
        // TODO
    }

    @Override
    public void endTurn() throws RemoteException, ActionNotAllowedException {
        serverGameController.endTurn(username);
    }

    @Override
    public void decideExcommunication(Boolean beExcommunicated) throws RemoteException, ActionNotAllowedException {
        serverGameController.decideExcommunication(username, beExcommunicated);
    }

    public String getUsername() {
        return username;
    }
}
