package client;

import client.exceptions.NetworkException;
import client.rmiclient.RMIClient;
import gamecontroller.GameController;
import gamecontroller.GameEventsInterface;
import gamecontroller.exceptions.ActionNotAllowedException;
import gamecontroller.exceptions.PlayerDoesNotExistException;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.Card;
import model.card.effects.ImmediateResourcesEffect;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.ClientToServerInterface;
import server.exceptions.LoginException;
import server.exceptions.NoAvailableGamesException;
import ui.UIInterface;
import ui.UIType;
import ui.cli.CommandLineUI;
import ui.cli.contexts.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class bridges the network interface, the UI and the game controller
 */
public class ClientController implements GameEventsInterface,
                                         NetworkSettingsContext.Callback,
                                         LoginContext.Callback,
                                         ChooseBonusTileContext.Callback,
                                         ChooseLeaderCardContext.Callback,
                                         MainTurnContext.Callback {

    private UIInterface ui;

    private GameController gameController = new GameController();

    private ClientToServerInterface clientConnection;

    private String ourUsername;

    public ClientController(UIType uiType) {
        switch (uiType) {
            case CLI:
                CommandLineUI cli = new CommandLineUI(this);
                ui = cli;
                cli.start();
                break;
        }
    }

    /* ---------------------------------------
     * Actions from the UI
     * --------------------------------------- */

    /**
     * {@link NetworkSettingsContext} callback.
     * Establishes a connection to the server
     *
     * @param connectionMethod the desired connection method
     * @param hostname the server hostname
     * @param port the server port
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void connect(ConnectionMethod connectionMethod, String hostname, int port) throws NetworkException, RemoteException {
        if (connectionMethod == ConnectionMethod.SOCKET) {
            // clientConnection = new SocketClient(hostname, port, this);
        }
        else if (connectionMethod == ConnectionMethod.RMI) {
            clientConnection = new RMIClient(hostname, port, this);
        }
        ui.showLoginPrompt();
    }

    /**
     * {@link LoginContext} callback.
     * Logs in the player with the given username
     *
     * @param username the username
     * @throws NetworkException
     * @throws LoginException
     * @throws RemoteException
     */
    @Override
    @SuppressWarnings("squid:S1166") // Suppress "rethrow this exception" warning
    public void login(String username) throws NetworkException, LoginException, RemoteException {
        ourUsername = username;
        clientConnection.loginPlayer(username);

        // TODO: maybe handle this not-very-exceptional condition without exceptions
        try {
            clientConnection.joinFirstAvailableGame();
        }
        catch (NoAvailableGamesException e) {
            clientConnection.createAndJoinGame();
        }
        ui.showWaitingMessage("Waiting for the game to start...");
    }

    /**
     * {@link ChooseBonusTileContext} callback.
     * Chooses a bonus tile during the draft
     *
     * @param bonusTile the chosen bonus tile
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    @Override
    public void chooseBonusTile(PersonalBonusTile bonusTile) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.choosePersonalBonusTile(bonusTile.getId());
    }

    /**
     * {@link ChooseLeaderCardContext} callback.
     * Chooses a leader card during the draft
     *
     * @param leaderCard the chosen leader card
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    @Override
    public void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.chooseLeaderCard(leaderCard.getId());
    }

    @Override
    public void goToFloor(Floor floor, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges, RequiredResourceSet paymentForCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.goToFloor(floor.getId(), familyMemberColor, councilPrivileges, paymentForCard);

        ui.showMainTurnContext();
    }


    public void chooseDevelopmentCardCouncilPrivileges(List<ObtainableResourceSet> councilPrivileges) throws NetworkException, ActionNotAllowedException, RemoteException {
        clientConnection.takeDevelopmentCard(gameController.getDevelopmentCardBeingTaken().getId(), councilPrivileges);

        ui.showMainTurnContext();
    }

    @Override
    public void goToActionSpace(ActionSpace actionSpace, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws NetworkException, RemoteException, ActionNotAllowedException {
        // TODO: locally check if the action is allowed
        clientConnection.goToActionSpace(actionSpace.getId(), familyMemberColor, chosenPrivileges);

        ui.showMainTurnContext();
    }

    @Override
    public void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.spendServants(servants);
    }

    @Override
    public void discardLeaderCard(LeaderCard leaderCard, ObtainableResourceSet councilPrivilege) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.discardLeaderCard(leaderCard.getId(), councilPrivilege);
    }

    @Override
    public void playLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.playLeaderCard(leaderCard.getId());
    }

    @Override
    public void activateOncePerRoundEffect(Card card, OncePerRoundEffectInterface effect) throws NetworkException, RemoteException, ActionNotAllowedException {
        //clientConnection.activateOncePerRoundEffect(card, effect);
    }

    @Override
    public void endTurn() throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.endTurn();
    }

    /* ---------------------------------------
     * GAME EVENTS
     * --------------------------------------- */

    public void showChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) {
        ui.showChoosePersonalBonusTile(personalBonusTiles);
    }

    public void showChooseLeaderCard(List<LeaderCard> leaderCards) {
        ui.showChooseLeaderCard(leaderCards);
    }

    public void showWaitingMessage(String message) throws RemoteException {
        ui.showWaitingMessage(message);
    }

    public void showChooseImmediateCouncilPrivileges(UUID cardId) {
        Card card = null;
        try {
            card = gameController.getLocalCardFromTowers(cardId);
        }
        catch (ActionNotAllowedException e) {
            e.printStackTrace();
        }

        // Check if the card gives council privileges
        List<ImmediateResourcesEffect> immediateResourcesEffects = card.getEffectsContainer().getEffectsImplementing(ImmediateResourcesEffect.class);

        int councilPrivilegesAmount = 0;

        List<ObtainableResourceSet> allowedCouncilPrivileges = getGame().getAllowedCouncilPrivileges();

        for(ImmediateResourcesEffect e : immediateResourcesEffects) {
            ObtainableResourceSet immediateResources = e.getObtainableResourceSet();
            councilPrivilegesAmount += immediateResources.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
        }

        if(councilPrivilegesAmount > 0) {
            ui.showChooseCouncilPrivileges(allowedCouncilPrivileges, councilPrivilegesAmount);
        }
        else {
            try {
                this.chooseDevelopmentCardCouncilPrivileges(new ArrayList<>());
            }
            catch (NetworkException e) {
                e.printStackTrace();
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (ActionNotAllowedException e) {
                e.printStackTrace();
            }
        }

    }

    public void abortGame(String errorMessage) {
        ui.showAbortGame(errorMessage);
    }

    @Override
    public void onPrepareNewRound() throws RemoteException {
        gameController.prepareNewRound();
        ui.onPrepareNewRound();
    }

    public void onSetGameConfiguration(Game game){
        gameController.setGame(game);
    }

    @Override
    public void onPlayerTurnStarted(String username) throws RemoteException {
        try {
            gameController.startPlayerTurn(username);
        }
        catch (PlayerDoesNotExistException e) {
            e.printStackTrace(); // This should never happen
        }

        if(username.equals(ourUsername)){
            ui.showMainTurnContext();
        }
        else {
            ui.onPlayerTurnStarted(username);
        }
    }

    @Override
    public void onCardsDrawn(List<UUID> territoryCardsIds, List<UUID> characterCardsIds, List<UUID> buildingCardsIds, List<UUID> ventureCardsIds) throws RemoteException {
        try {
            gameController.setDevelopmentCards(territoryCardsIds, characterCardsIds, buildingCardsIds, ventureCardsIds);
        }
        catch (ActionNotAllowedException e) {
            e.printStackTrace();
        }
        ui.onCardsDrawn(territoryCardsIds, characterCardsIds, buildingCardsIds, ventureCardsIds);
    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {
        gameController.setDiceValues(blackDie, whiteDie, orangeDie);
        ui.onDiceThrown(blackDie, whiteDie, orangeDie);
    }

    @Override
    public void onPlayerSpendsServants(String username, int servants) throws RemoteException {
        try {
            gameController.spendServants(username, servants);
        }
        catch (ActionNotAllowedException e) {
            e.printStackTrace();
        }

        ui.onPlayerSpendsServants(username, servants);
    }

    @Override
    public void onPlayerOccupiesActionSpace(String username, UUID actionSpaceId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        try {
            gameController.goToActionSpace(username, actionSpaceId, familyMemberColor, councilPrivileges);
        }
        catch (ActionNotAllowedException e) {
            e.printStackTrace();
        }
        ui.onPlayerOccupiesActionSpace(username, actionSpaceId, familyMemberColor, councilPrivileges);
    }

    @Override
    public void onPlayerOccupiesFloor(String username, UUID floorId, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges, RequiredResourceSet paymentForCard) throws RemoteException {
        try {
            gameController.goToFloor(username, familyMemberColor, floorId, paymentForCard, chosenPrivileges);
            ui.onPlayerOccupiesFloor(username, floorId, familyMemberColor, chosenPrivileges, paymentForCard);
        }
        catch (ActionNotAllowedException e) {
            // This should never happen
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerTakesDevelopmentCard(String username, UUID cardId, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        try {
            gameController.takeDevelopmentCard(username, cardId, councilPrivileges);
        }
        catch (ActionNotAllowedException e) {
            e.printStackTrace();
            // This should never happen
        }

        ui.onPlayerTakesDevelopmentCard(username, cardId, councilPrivileges);
    }

    /**
     * Utility getter for the UI
     */
    public Game getGame() {
        return gameController.getGame();
    }

    public GameController getGameController() {
        return gameController;
    }


}
