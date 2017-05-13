package it.polimi.ingsw.lorenzo.client;

import it.polimi.ingsw.lorenzo.client.exceptions.NetworkException;
import it.polimi.ingsw.lorenzo.client.socketclient.SocketClient;
import it.polimi.ingsw.lorenzo.gamecontroller.GameController;
import ui.UIEventsInterface;
import ui.cli.contexts.NetworkSettingsContext;

public class ClientController implements NetworkSettingsContext.Callback {
    UIEventsInterface ui;

    GameController gameController;

    ClientActionsInterface client;

    public ClientController(UIEventsInterface ui){
        this.ui = ui;
    }

    public void connect(ConnectionMethod connectionMethod, String hostname, int port) throws NetworkException {
        if(connectionMethod == ConnectionMethod.SOCKET){
            this.client = new SocketClient(hostname, port/*, gameController*/);
        }
    }
}
