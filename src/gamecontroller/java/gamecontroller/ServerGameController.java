package gamecontroller;

import gamecontroller.exceptions.ActionNotAllowedException;
import gamecontroller.exceptions.LeaderCardNotAvailableException;
import gamecontroller.exceptions.PersonalBonusTileNotAvailableException;
import model.Excommunication;
import model.Game;
import model.card.leader.LeaderCard;
import model.player.PersonalBonusTile;
import model.player.Player;
import server.ClientConnection;
import server.GameRoom;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class extends {@link GameController} to provide server specific functionality
 */
public class ServerGameController extends GameController {
    private GameRoom gameRoom;

    /**
     * At the beginning of the game each player is given 4 leader cards.
     * They choose 1 card to keep and pass the others to the next player.
     * This object is used to manage the groups of cards during the draft.
     */
    private Map<Player, List<LeaderCard>> leaderCardsDraft;

    /**
     * This object is used to track which players have not yet made a choice
     * in situations which require all of them to choose simultaneously
     * (such as during the leader cards draft)
     */
    private List<Player> playersThatHaveToDraft;

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
    public void setPersonalBonusTile(Player player, PersonalBonusTile personalBonusTile) throws PersonalBonusTileNotAvailableException, ActionNotAllowedException {
        // Check that the game phase is right
        if(getGameState() != GameState.DRAFTING_BONUS_TILES){
            throw new ActionNotAllowedException();
        }

        // Check that the bonus tile is available
        Optional<PersonalBonusTile> optChosenPersonalBonusTile = getGame().getAvailablePersonalBonusTiles().stream()
                                                                       .filter(tile -> tile.equals(personalBonusTile))
                                                                       .findFirst();

        if (!optChosenPersonalBonusTile.isPresent()) {
            throw new PersonalBonusTileNotAvailableException();
        }

        PersonalBonusTile chosenPersonalBonusTile = optChosenPersonalBonusTile.get();

        super.setPersonalBonusTile(player, chosenPersonalBonusTile);

        // Remove the chosen bonus tile from the available ones
        getGame().getAvailablePersonalBonusTiles().remove(chosenPersonalBonusTile);

        // Draft next bonus tile
        int currentPlayerIndex = getGame().getPlayers().indexOf(player);
        if(currentPlayerIndex > 0) {
            Player nextPlayer = getGame().getPlayers().get(currentPlayerIndex - 1);
            getGame().setCurrentPlayer(nextPlayer);
            draftNextBonusTile();
        }
        else {
            startLeaderCardsDraft();
        }
    }

    /**
     * Start drafting the leader cards
     */
    private void startLeaderCardsDraft(){
        setGameState(GameState.DRAFTING_LEADER_CARDS);

        // Draw 4 leader cards for each player
        leaderCardsDraft = new HashMap<>();
        List<LeaderCard> availableLeaderCards = getGame().getAvailableLeaderCards();
        for (Player player : getGame().getPlayers()) {
            List<LeaderCard> playerLeaderCards = new ArrayList<>();
            for (int i=0; i<4; i++) {
                LeaderCard leaderCard = availableLeaderCards.get(new Random().nextInt(availableLeaderCards.size()));
                playerLeaderCards.add(leaderCard);
                availableLeaderCards.remove(leaderCard);
            }
            leaderCardsDraft.put(player, playerLeaderCards);
        }

        playersThatHaveToDraft = (List)getGame().getPlayers().clone();

        draftNextLeaderCard();
    }

    /**
     * Draft the next leader card for the remaining players
     */
    private void draftNextLeaderCard() {
        for (Player player : playersThatHaveToDraft) {
            ClientConnection playerConnection = gameRoom.getConnectionForPlayer(player);

            List<LeaderCard> playerLeaderCards = leaderCardsDraft.get(player);

            try {
                playerConnection.askToChooseLeaderCard(playerLeaderCards);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when a player chooses his leader card
     * @param player
     * @param leaderCard
     */
    public void addLeaderCard(Player player, LeaderCard leaderCard) throws LeaderCardNotAvailableException, ActionNotAllowedException {
        // Check that the game phase is right
        if(getGameState() != GameState.DRAFTING_LEADER_CARDS){
            throw new ActionNotAllowedException();
        }

        // Check that the player has not chosen a leader card yet
        if(!playersThatHaveToDraft.contains(player)){
            throw new ActionNotAllowedException();
        }

        // Check that the player could choose this leader card
        Optional<LeaderCard> optChosenLeaderCard = leaderCardsDraft.get(player).stream()
                                                                    .filter(tile -> tile.equals(leaderCard))
                                                                    .findFirst();

        if (!optChosenLeaderCard.isPresent()) {
            throw new LeaderCardNotAvailableException();
        }
        LeaderCard chosenLeaderCard = optChosenLeaderCard.get();

        // Add the chosen leader card to the player's available ones
        player.getAvailableLeaderCards().add(chosenLeaderCard);

        // Remove the chosen leader card from the possible choices
        leaderCardsDraft.get(player).remove(chosenLeaderCard);

        // Remove the player from the ones that have to make a choice
        playersThatHaveToDraft.remove(player);

        // Check if all the players have chosen their leader card
        if(playersThatHaveToDraft.isEmpty()) {

            // Check if the drafting phase is concluded
            int remainingChoices = leaderCardsDraft.get(player).size();
            if (remainingChoices == 0) {
                // TODO: next game phase
                System.out.println("TODO: phase after leader cards draft");
                return;
            }

            // Rotate the available leader cards choices
            ArrayList<Player> players = getGame().getPlayers();
            List<LeaderCard> tmp = null;
            for(Player p: players.stream()
                                    .limit(players.size() - 1)
                                    .collect(Collectors.toList())){

                int nextPlayerIndex = players.indexOf(p) + 1;
                Player nextPlayer = players.get(nextPlayerIndex);
                tmp = leaderCardsDraft.get(nextPlayer);
                leaderCardsDraft.put(nextPlayer, leaderCardsDraft.get(p));
            }

            // Pass the last player's cards to the first
            Player firstPlayer = players.get(0);
            leaderCardsDraft.put(firstPlayer, tmp);

            // All the players have to draft again
            playersThatHaveToDraft = (List)getGame().getPlayers().clone();

            // Ask the players to draft the next leader card
            draftNextLeaderCard();
        }
        else {
            try {
                gameRoom.getConnectionForPlayer(player).showWaitingMessage("Waiting for other players to choose...");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }



    }
}
