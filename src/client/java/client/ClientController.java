package client;

import client.exceptions.NetworkException;
import client.socketclient.SocketClient;
import client.exceptions.LoginException;
import client.exceptions.NoAvailableRoomsException;
import client.rmiclient.RMIClient;
import gamecontroller.GameController;
import gamecontroller.GameEventsInterface;
import model.Game;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.effects.interfaces.OncePerTurnEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainedResourceSet;
import server.ClientToServerInterface;
import ui.UIEventsInterface;
import ui.cli.contexts.LoginContext;
import ui.cli.contexts.NetworkSettingsContext;

import java.rmi.RemoteException;

public class ClientController implements GameEventsInterface, NetworkSettingsContext.Callback, LoginContext.Callback {
    private UIEventsInterface ui;

    private GameController gameController;

    private ClientToServerInterface clientConnection;

    public ClientController(UIEventsInterface ui){
        this.ui = ui;
    }

    public void connect(ConnectionMethod connectionMethod, String hostname, int port) throws NetworkException, RemoteException {
        if(connectionMethod == ConnectionMethod.SOCKET){
            clientConnection = new SocketClient(hostname, port, this);
            ui.showLoginPrompt();
        }
        else if(connectionMethod == ConnectionMethod.RMI){
            clientConnection = new RMIClient(hostname, port, this);
            ui.showLoginPrompt();

        }
    }

    @Override
    public void login(String username) throws NetworkException, LoginException, RemoteException {
        clientConnection.loginPlayer(username);

        try {
            clientConnection.joinFirstAvailableRoom();
        } catch (NoAvailableRoomsException e) {
            clientConnection.createAndJoinRoom();
        }
        ui.showWaitingForGameToStart();
    }

    @Override
    public void onGameStart(Game g) {

    }

    @Override
    public void onDicesThrown(int black, int white, int orange) {

    }

    @Override
    public void onPlayerTurnStart(Player player) {

    }

    @Override
    public void playerSkipsTurn(Player player) {

    }

    @Override
    public void onPlayerSpentServants(Player player, int servants) {

    }

    @Override
    public void onPlayerGoesToFloor(Player player, FamilyMemberColor familyMemberColor, Floor floor) {

    }

    @Override
    public void goToSmallHarvest(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToBigHarvest(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToSmallProducion(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToBigProducion(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToCouncilPalace(Player player, FamilyMemberColor familyMemberColor) {

    }

    @Override
    public void goToMarket(Player player, FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) {

    }

    @Override
    public void onPlayerPlacesFamilyMember(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) {

    }

    @Override
    public void onPlayerPlayedLeaderCard(Player player, LeaderCard leaderCard) {

    }

    @Override
    public void onPlayerDiscardsLeaderCard(Player player, LeaderCard leaderCard) {

    }

    @Override
    public <T extends OncePerTurnEffectInterface> void onPlayerActivatesOncePerTurnEffect(Player player, T effect) {

    }

    @Override
    public void onPlayerGetsResources(Player player, ObtainedResourceSet obtainedResourceSet) {

    }

    @Override
    public void onPlayerOccupiesActionSpace(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) {

    }

    @Override
    public void onPlayerPerformsAction(Player player, Action action) {

    }
}
