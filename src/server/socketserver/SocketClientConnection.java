package server.socketserver;

import gamecontroller.GameState;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import server.ServerGameController;
import model.Game;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.player.Player;
import server.ClientConnection;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class is responsible for handling communication with a single client.
 * It reads the actions from the
 */
public class SocketClientConnection extends ClientConnection implements Runnable {
    private Socket connection;
    private BufferedReader input;
    private BufferedWriter output;

    public SocketClientConnection(List<ServerGameController> gameRooms, Socket connection) throws IOException {
        super(gameRooms);
        this.connection = connection;
        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
    }

    @Override
    @SuppressWarnings("squid:S2189") // Disable infinite loop warning
    public void run() {
        while (true) {
            try {
                String in = input.readLine();

            }
            catch (IOException e) {
            }
        }
    }

    @Override
    public void pingClient() throws RemoteException {

    }

    @Override
    public void abortGame(String errorMessage) throws RemoteException {

    }

    @Override
    public void onGameStateChange(GameState gameState) {

    }

    @Override
    public void onTurnOrderChanged(List<Player> playerOrder) throws RemoteException {

    }

    @Override
    public void onPlayerTurnStarted(Player player) throws RemoteException {

    }

    @Override
    public void showWaitingMessage(String message) throws RemoteException {

    }

    @Override
    public void askToChoosePersonalBonusTile(List<PersonalBonusTile> personalBonusTiles) throws RemoteException {

    }

    @Override
    public void askToChooseLeaderCard(List<LeaderCard> leaderCards) throws RemoteException {

    }

    @Override
    public void setGameConfiguration(Game game) throws RemoteException {

    }

    @Override
    public void onCardsDrawn(List<TerritoryCard> territoryCards, List<CharacterCard> characterCards, List<BuildingCard> buildingCards, List<VentureCard> ventureCards) throws RemoteException {

    }

    @Override
    public void onDiceThrown(int blackDie, int whiteDie, int orangeDie) throws RemoteException {

    }
}
