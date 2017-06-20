package server;

import gamecontroller.GameEventsInterface;
import gamecontroller.GameState;
import gamecontroller.ServerGameController;
import model.player.Player;
import model.player.PlayerColor;
import server.configloader.ConfigLoader;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * This class represents a game room
 */
public class GameRoom implements GameEventsInterface {
    /**
     * The list of connections to the players
     */
    private ArrayList<ClientConnection> connections = new ArrayList<>();

    /**
     * The controller for the game of this room
     */
    private ServerGameController serverGameController = new ServerGameController(this);

    /**
     * The name of the room
     */
    private String name;

    /**
     * The time to wait (in milliseconds) before starting the game after two players have joined the room.
     * TODO: 5/30/17 make this timeout configurable
     */
    private int gameStartTimeout = 5000;

    private Thread roomTimeoutTimer;

    public GameRoom(String name) {
        this.name = name;
    }

    /**
     * Callback called when the room timeout expires
     */
    private void onRoomTimeout() {
        System.out.println("Room " + name + " timeout expired, starting game...");
        // Load game configuration
        loadConfiguration();

        // Create players and set colors
        createPlayers();

        // Extract excommunications
        serverGameController.drawExcommunications();

        // Set random turn order
        serverGameController.shufflePlayers();

        // Start personal bonus tile draft
        serverGameController.draftNextBonusTile();
    }

    /**
     * Load the game configuration
     */
    private void loadConfiguration() {
        try {
            serverGameController.setGame(ConfigLoader.loadConfiguration("configuration"));
        } catch (IOException e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    /**
     * Create players and assign colors
     */
    private void createPlayers() {
        PlayerColor[] colors = PlayerColor.values();
        int i = 0;
        for (ClientConnection connection : connections) {
            Player player = new Player(connection.getUsername());

            // Set player color
            player.setColor(colors[i]);

            // Add player to the game
            serverGameController.addPlayer(player);
            connection.setPlayer(player);

            i++;
        }
    }

    /**
     * Signal state change to all players
     * @param gameState
     */
    @Override
    public void onGameStateChange(GameState gameState) {
        for (ClientConnection connection: connections) {
            try {
                connection.onGameStateChange(gameState);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAvailable() {
        return (serverGameController.isGameStarting() && !this.isFull());
    }

    public boolean isFull() {
        return (connections.size() >= 4);
    }

    public ClientConnection getConnectionForPlayer (Player player) {
        for (ClientConnection connection : connections) {
            // TODO: 6/13/17 implement equals for players
            if (connection.getPlayer().getUsername().equals(player.getUsername()))
                return connection;
        }
        throw new NoSuchElementException();
    }

    public ArrayList<ClientConnection> getConnections() {
        return connections;
    }

    /**
     * Adds a player to the room, starting the room timeout if
     * there are at least 2 players connected
     * @param clientConnection
     */
    public void addPlayer(ClientConnection clientConnection) {
        connections.add(clientConnection);
        if (connections.size() >= 2 && roomTimeoutTimer == null) {
            System.out.println("Starting room " + name + " timeout");
            roomTimeoutTimer = new Thread(new RoomTimerClass());
            roomTimeoutTimer.start();
        }
    }

    public ServerGameController getServerGameController() {
        return serverGameController;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private class RoomTimerClass implements Runnable {
        @Override
        public void run() {
            // The time when the timer was started, needed to handle exceptions
            long startTime;
            long remainingTime = gameStartTimeout;

            while(true){
                startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(remainingTime);
                    onRoomTimeout();
                    break;
                } catch (InterruptedException e) {
                    // If somehow the thread is woken up early compute the
                    // remaining time and go back to sleep again
                    remainingTime = remainingTime - (System.currentTimeMillis() - startTime);
                }
            }
        }
    }
}
