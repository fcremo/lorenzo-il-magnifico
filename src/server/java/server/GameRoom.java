package server;

import com.google.gson.stream.JsonReader;
import gamecontroller.GameEventsInterface;
import gamecontroller.GameState;
import gamecontroller.ServerGameController;
import model.player.Player;
import model.player.PlayerColor;
import server.configloader.ConfigLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * This class represents a game room
 */
public class GameRoom implements GameEventsInterface {
    private static final Logger LOGGER = Logger.getLogger("GameRoom");

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
     */
    private int gameStartTimeout;

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
        String configDirectory = "configuration";

        // Load timeout
        try {
            InputStreamReader timeoutFileReader = new InputStreamReader(new FileInputStream(configDirectory + "/timeouts.json"));
            JsonReader reader = new JsonReader(timeoutFileReader);
            reader.beginObject();
            while (reader.hasNext()){
                String key = reader.nextName();
                String val = reader.nextString();
                if("startTimeout".equals(key)){
                    this.gameStartTimeout = Integer.parseInt(val) * 1000;
                }
            }
            reader.close();
            timeoutFileReader.close();
        }
        catch (IOException e) {
            LOGGER.warning("Cannot read timeout configuration file!");
            this.gameStartTimeout = 5000;
        }
        catch (NumberFormatException e) {
            LOGGER.warning("Error while parsing room timeout from file!");
            this.gameStartTimeout = 5000;
        }

        // Load game configuration
        try {
            serverGameController.setGame(ConfigLoader.loadConfiguration(configDirectory));
        } catch (IOException e) {
            LOGGER.severe("Error while loading game configuration from file, cannot start the game!");
            for (ClientConnection c: connections) {
                try {
                    c.abortGame("Cannot load game configuration!");
                } catch (RemoteException e1) {
                    // We're aborting the game, we don't really care for remote exceptions
                }
            }
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
        Optional<ClientConnection> connection = connections.stream()
                                                            .filter(p -> p.getPlayer().equals(player))
                                                            .findFirst();
        if(connection.isPresent()){
            return connection.get();
        }
        else {
            throw new NoSuchElementException();
        }
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
