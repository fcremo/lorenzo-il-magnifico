package server;

import client.ServerToClientInterface;

public abstract class ClientConnection implements ServerToClientInterface {
    private ServerController controller;

    public ServerController getController() {
        return controller;
    }

    public void setController(ServerController controller) {
        this.controller = controller;
    }
}
