package gamecontroller;

import gamecontroller.exceptions.PersonalBonusTileNotAvailableException;
import model.Excommunication;
import model.Game;
import model.player.Player;
import model.player.PersonalBonusTile;
import server.ClientConnection;
import server.GameRoom;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class extends {@link GameController} to provide server specific functionality
 */
public class ServerGameController extends GameController {
    private GameRoom gameRoom;

    public ServerGameController(GameRoom gameRoom) {
        super(gameRoom);
        this.gameRoom = gameRoom;
    }

    /**
     * Set a random player order and update current player
     */
    public void shufflePlayers(){
        Collections.shuffle(getGame().getPlayers());
        Player nextPlayer = getGame().getPlayers().get(getGame().getPlayers().size()-1);
        getGame().setCurrentPlayer(nextPlayer);
    }

    /**
     * Draw three random excommunications
     */
    public void drawExcommunications(){
        Game game = getGame();

        // First period
        List<Excommunication> firstPeriodExcommunications = game.getAvailableExcommunications().stream()
                .filter(e -> e.getPeriod() == 1).collect(Collectors.toList());
        Excommunication firstPeriodExcommunication = firstPeriodExcommunications.get(new Random().nextInt(firstPeriodExcommunications.size()));

        Excommunication[] chosenExcommunications = {firstPeriodExcommunication, firstPeriodExcommunication, firstPeriodExcommunication};
        game.getBoard().setExcommunications(chosenExcommunications);
        /*
        // Second period
        List<Excommunication> secondPeriodExcommunications = game.getAvailableExcommunications().stream()
                .filter(e -> e.getPeriod() == 2).collect(Collectors.toList());
        Excommunication secondPeriodExcommunication = secondPeriodExcommunications.get(new Random().nextInt(secondPeriodExcommunications.size()));

        // Third period
        List<Excommunication> thirdPeriodExcommunications = game.getAvailableExcommunications().stream()
                .filter(e -> e.getPeriod() == 3).collect(Collectors.toList());
        Excommunication thirdPeriodExcommunication = thirdPeriodExcommunications.get(new Random().nextInt(thirdPeriodExcommunications.size()));

        // Set chosen excommunications in the game
        Excommunication[] chosenExcommunications = {firstPeriodExcommunication, secondPeriodExcommunication, thirdPeriodExcommunication};
        game.getBoard().setExcommunications(chosenExcommunications);
         */
    }

    @Override
    public void setGameState(GameState gameState) {
        super.setGameState(gameState);
        gameRoom.onGameStateChange(gameState);
    }

    /**
     * Start drafting the personal bonus tiles
     */
    public void draftNextBonusTile(){
        setGameState(GameState.DRAFTING_BONUS_TILES);
        ClientConnection connection = gameRoom.getConnectionForPlayer(getGame().getCurrentPlayer());
        try {
            connection.askToChoosePersonalBonusTile(getGame().getAvailablePersonalBonusTiles());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when a player chooses his personal bonus tile
     * @param player
     * @param personalBonusTile
     */
    public void setPersonalBonusTile(Player player, PersonalBonusTile personalBonusTile) throws PersonalBonusTileNotAvailableException {
        // Check that the personal bonus tile is available
        if (!getGame().getAvailablePersonalBonusTiles().contains(personalBonusTile)) {
            throw new PersonalBonusTileNotAvailableException();
        }

        super.setPersonalBonusTile(player, personalBonusTile);

        // Remove the chosen bonus tile from the available ones
        getGame().getAvailablePersonalBonusTiles().remove(personalBonusTile);

        // Draft next bonus tile
        int currentPlayerIndex = getGame().getPlayers().indexOf(player);
        if(currentPlayerIndex > 0) {
            Player nextPlayer = getGame().getPlayers().get(currentPlayerIndex - 1);
            getGame().setCurrentPlayer(nextPlayer);
            draftNextBonusTile();
        }
        else {
            // TODO: 6/13/17
            draftLeaderCards();
        }
    }

    private void draftLeaderCards(){
        System.out.println("DRAFT LEADER CARDS REACHED");
    }
}
