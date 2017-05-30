package server;

import client.ServerToClientInterface;

/**
 * This abstract class provides the server an abstraction of a client connection.
 * It's meant to be extended by other classes implementing the ServerToClientInterface
 * and a network protocol for communicating with the clients.
 */
public abstract class ClientConnection implements ServerToClientInterface {
    /**
     * The server controller that will be receiving events from the client
     */
    private ServerController controller;

    public ServerController getController() {
        return controller;
    }

    public void setController(ServerController controller) {
        this.controller = controller;
    }
}
