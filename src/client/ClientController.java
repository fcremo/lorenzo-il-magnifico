package client;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import client.rmiclient.RMIClient;
import client.socketclient.SocketClient;
import gamecontroller.GameController;
import gamecontroller.GameEventsInterface;
import gamecontroller.GameState;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.Card;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.player.Player;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.ClientToServerInterface;
import server.exceptions.ActionNotAllowedException;
import ui.UIInterface;
import ui.UIType;
import ui.cli.CLIUserInterface;
import ui.cli.contexts.*;

import java.rmi.RemoteException;
import java.util.List;

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

    private Player ourPlayer;
    private String ourUsername;

    public ClientController(UIType uiType) {
        switch (uiType) {
            case CLI:
                CLIUserInterface cli = new CLIUserInterface(this);
                ui = cli;
                cli.start();
                break;
        }
    }

    /* ---------------------------------------
     * CALLBACKS
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
            clientConnection = new SocketClient(hostname, port, this);
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
            clientConnection.joinFirstAvailableRoom();
        }
        catch (NoAvailableRoomsException e) {
            clientConnection.createAndJoinRoom();
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
        clientConnection.choosePersonalBonusTile(bonusTile);
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
        clientConnection.chooseLeaderCard(leaderCard);
    }

    @Override
    public void goToCouncilPalace(FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws NetworkException, RemoteException, ActionNotAllowedException {
        // TODO: locally check if the action is allowed
        clientConnection.goToCouncilPalace(familyMemberColor, chosenPrivileges);
    }

    @Override
    public void goToFloor(Floor floor, FamilyMemberColor familyMember, RequiredResourceSet paymentForCard) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    @Override
    public void goToMarket(FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) throws NetworkException, RemoteException, ActionNotAllowedException {

    }

    /*@Override
    public void goToFloor(Floor floor, FamilyMemberColor familyMember) throws NetworkException, RemoteException, ActionNotAllowedException {
        if(!gameController.canGoThere(ourPlayer, familyMember, floor)) throw new ActionNotAllowedException("You cannot go there!");

        clientConnection.goToFloor(floor, familyMember);
    }*/

    @Override
    public void goToSmallHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.goToSmallHarvest(familyMemberColor);
    }

    @Override
    public void goToBigHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.goToBigHarvest(familyMemberColor);
    }

    @Override
    public void goToSmallProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.goToSmallProduction(familyMemberColor);
    }

    @Override
    public void goToBigProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.goToBigProduction(familyMemberColor);
    }

    @Override
    public void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.spendServants(servants);
    }

    @Override
    public void discardLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.discardLeaderCard(leaderCard);
    }

    @Override
    public void playLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException {
        clientConnection.playLeaderCard(leaderCard);
    }

    @Override
    public void activateOncePerRoundEffect(Card card, OncePerRoundEffectInterface effect) throws NetworkException, RemoteException, ActionNotAllowedException {
        //clientConnection.activateOncePerRoundEffect(card, effect);
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

    public void abortGame(String errorMessage) {
        ui.showAbortGame(errorMessage);
    }

    @Override
    public void onGameStateChange(GameState gameState) throws RemoteException {
        gameController.setGameState(gameState);
        ui.onGameStateChange(gameState);
    }

    public void onSetGameConfiguration(Game game){
        gameController.setGame(game);
        ourPlayer = gameController.getGame().getPlayers().stream()
                                  .filter(p -> p.getUsername().equals(ourUsername))
                                  .findFirst()
                                  .get();
    }

    @Override
    public void onTurnOrderChanged(List<Player> playerOrder) throws RemoteException {
        gameController.getGame().setPlayers(playerOrder);
        ui.onTurnOrderChanged(playerOrder);
    }

    @Override
    public void onPlayerTurnStarted(Player player) throws RemoteException {
        gameController.getGame().setCurrentPlayer(player);
        if(player.getUsername().equals(ourUsername)){
            ui.showMainTurnContext();
        }
        else {
            ui.onPlayerTurnStarted(player);
        }
    }

    @Override
    public void onCardsDrawn(List<TerritoryCard> territoryCards, List<CharacterCard> characterCards, List<BuildingCard> buildingCards, List<VentureCard> ventureCards) throws RemoteException {
        gameController.setDevelopmentCards(territoryCards, characterCards, buildingCards, ventureCards);
        ui.onCardsDrawn(territoryCards, characterCards, buildingCards, ventureCards);
    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {
        gameController.setDiceValues(blackDie, whiteDie, orangeDie);
        ui.onDiceThrown(blackDie, whiteDie, orangeDie);
    }

    @Override
    public void onPlayerOccupiesCouncilPalace(Player player, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws RemoteException {
        try {
            gameController.goToCouncilPalace(player, familyMemberColor, councilPrivileges);
        }
        catch (ActionNotAllowedException e) {
            e.printStackTrace();
        }
        ui.onPlayerOccupiesCouncilPalace(player, familyMemberColor, councilPrivileges);
    }

    @Override
    public void onPlayerOccupiesFloor(Player player, FamilyMemberColor familyMemberColor, Floor floor, RequiredResourceSet paymentForCard) throws RemoteException {

    }

    /**
     * Utility getter for the UI
     */
    public Game getGame() {
        return gameController.getGame();
    }
}
