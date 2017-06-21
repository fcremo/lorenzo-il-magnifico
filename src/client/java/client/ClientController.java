package client;

import client.exceptions.LoginException;
import client.exceptions.NetworkException;
import client.exceptions.NoAvailableRoomsException;
import client.rmiclient.RMIClient;
import client.socketclient.SocketClient;
import gamecontroller.GameController;
import gamecontroller.GameEventsInterface;
import gamecontroller.GameState;
import gamecontroller.exceptions.ActionNotAllowedException;
import gamecontroller.exceptions.LeaderCardNotAvailableException;
import gamecontroller.exceptions.PersonalBonusTileNotAvailableException;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import server.ClientToServerInterface;
import ui.UIEventsInterface;
import ui.cli.contexts.ChooseBonusTileContext;
import ui.cli.contexts.ChooseLeaderCardContext;
import ui.cli.contexts.LoginContext;
import ui.cli.contexts.NetworkSettingsContext;

import java.rmi.RemoteException;
import java.util.List;

public class ClientController implements GameEventsInterface, NetworkSettingsContext.Callback, LoginContext.Callback,
                                            ChooseBonusTileContext.Callback, ChooseLeaderCardContext.Callback {
    private UIEventsInterface ui;

    private GameController gameController;

    private ClientToServerInterface clientConnection;

    public ClientController(UIEventsInterface ui) {
        this.ui = ui;
    }

    /* ---------------------------------------
     * CALLBACKS
     * --------------------------------------- */
    /**
     * NetworkSettingsContext callback.
     * Establishes a connection to the server
     * @param connectionMethod
     * @param hostname
     * @param port
     * @throws NetworkException
     * @throws RemoteException
     */
    @Override
    public void connect(ConnectionMethod connectionMethod, String hostname, int port) throws NetworkException, RemoteException {
        if (connectionMethod == ConnectionMethod.SOCKET) {
            clientConnection = new SocketClient(hostname, port, this);
        } else if (connectionMethod == ConnectionMethod.RMI) {
            clientConnection = new RMIClient(hostname, port, this);
        }
        ui.showLoginPrompt();
    }

    /**
     * {@link LoginContext} callback.
     * Logs in the player with the given username
     * @param username
     * @throws NetworkException
     * @throws LoginException
     * @throws RemoteException
     */
    @Override
    public void login(String username) throws NetworkException, LoginException, RemoteException {
        clientConnection.loginPlayer(username);

        try {
            clientConnection.joinFirstAvailableRoom();
        } catch (NoAvailableRoomsException e) {
            clientConnection.createAndJoinRoom();
        }
        ui.showWaitingMessage("Waiting for the game to start...");
    }

    @Override
    public void chooseBonusTile(PersonalBonusTile bonusTile) throws NetworkException, RemoteException {
        try {
            clientConnection.choosePersonalBonusTile(bonusTile);
        } catch (PersonalBonusTileNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chooseLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, LeaderCardNotAvailableException, ActionNotAllowedException {
        clientConnection.chooseLeaderCard(leaderCard);
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
        ui.onGameStateChange(gameState);
    }
}
