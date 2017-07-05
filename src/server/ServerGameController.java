package server;

import gamecontroller.GameController;
import gamecontroller.GameState;
import gamecontroller.utils.StreamUtils;
import model.Excommunication;
import model.Game;
import model.board.actionspace.*;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.PersonalBonusTile;
import model.player.Player;
import model.player.PlayerColor;
import model.resource.ObtainableResource;
import server.configloader.ConfigLoader;
import server.exceptions.ActionNotAllowedException;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.PersonalBonusTileNotAvailableException;
import server.exceptions.RoomNotJoinableException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides the server specific logic for the game,
 * and uses the {@link GameController} class for logic shared with the clients
 */
public class ServerGameController {
    private static final Logger LOGGER = Logger.getLogger("ServerGameController");

    /**
     * The controller for the game being played
     */
    private GameController gameController = new GameController();

    /**
     * The list of connections to the players
     */
    private List<ClientConnection> connections = new ArrayList<>();

    /**
     * The time to wait (in milliseconds) before starting the game after two players have joined the room.
     */
    private int gameStartTimeout;

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

    /**
     * Callback called when the room timeout expires
     */
    private void startGame() {
        LOGGER.info("Room timeout expired, starting game...");
        // Load game configuration
        loadConfiguration();

        // Create players and set colors
        createPlayers();

        // Extract excommunications
        drawExcommunications();

        // Set random turn order
        shufflePlayers();

        // Start personal bonus tile draft phase (in inverse turn order)
        startPersonalBonusTileDraft();
    }

    /**
     * Load the game configuration
     */
    private void loadConfiguration() {
        String configDirectory = "configuration";

        ConfigLoader configLoader = new ConfigLoader(configDirectory);
        try {
            configLoader.loadConfiguration();
        }
        catch (IOException e) {
            LOGGER.severe("Error while loading game configuration from file, cannot start the game!");

            // Inform all players that the game cannot start
            for (ClientConnection c : connections) {
                try {
                    c.abortGame("Cannot load game configuration!");
                }
                catch (RemoteException e1) {
                    // We're aborting the game, we don't really care for remote exceptions
                }
            }
        }

        gameController.setGame(configLoader.getGame());

        // Disable some action spaces if players are less than four
        if(connections.size() < 4) {
            getGame().getBoard().getMarket4().setEnabled(false);
        }
        if (connections.size() < 3) {
            getGame().getBoard().getMarket3().setEnabled(false);
            getGame().getBoard().getBigHarvestArea().setEnabled(false);
            getGame().getBoard().getBigProductionArea().setEnabled(false);
        }

        gameStartTimeout = configLoader.getGameStartTimeout();
    }

    /**
     * Create players and assign colors
     */
    private void createPlayers() {
        PlayerColor[] colors = PlayerColor.values();
        int i = 0;
        for (ClientConnection connection : connections) {
            Player player = new Player(connection.getUsername());
            player.setColor(colors[i]);

            connection.setPlayer(player);

            // Add player to the game
            getGame().addPlayer(player);

            i++;
        }
    }

    /**
     * Set a random player order and update current player
     */
    private void shufflePlayers() {
        Collections.shuffle(getGame().getPlayers());
    }

    /**
     * Start the personal bonus tile draft
     */
    private void startPersonalBonusTileDraft() {
        setGameState(GameState.DRAFTING_BONUS_TILES);
        Player nextPlayer = getGame().getPlayers().get(getGame().getPlayers().size() - 1);
        getGame().setCurrentPlayer(nextPlayer);
        draftNextBonusTile();
    }

    /**
     * Draw three random excommunications
     */
    private void drawExcommunications() {
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

    /**
     * Change the state of the game and inform all players
     * @param gameState
     */
    public void setGameState(GameState gameState) {
        gameController.setGameState(gameState);
        for (ClientConnection connection : connections) {
            try {
                connection.onGameStateChange(gameState);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start drafting the personal bonus tiles
     */
    private void draftNextBonusTile() {
        // Ask the current player to choose a bonus tile
        ClientConnection connection = getConnectionForPlayer(getGame().getCurrentPlayer());
        try {
            connection.askToChoosePersonalBonusTile(getGame().getAvailablePersonalBonusTiles());
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        // Put the others to wait
        getGame().getPlayers().stream()
                 .filter(player -> !player.equals(getGame().getCurrentPlayer()))
                 .forEach(player -> {
                     try {
                         getConnectionForPlayer(player).showWaitingMessage("Wait for other players to choose a bonus tile");
                     }
                     catch (RemoteException e) {
                         e.printStackTrace();
                     }
                 });
    }

    /**
     * Called when a player chooses his personal bonus tile
     *
     * @param player
     * @param personalBonusTile
     */
    public void setPersonalBonusTile(Player player, PersonalBonusTile personalBonusTile) throws ActionNotAllowedException {
        assertGameState(GameState.DRAFTING_BONUS_TILES);

        // Check that the choice is legitimate
        Optional<PersonalBonusTile> optChosenPersonalBonusTile = getGame().getAvailablePersonalBonusTiles().stream()
                                                                          .filter(tile -> tile.equals(personalBonusTile))
                                                                          .findFirst();
        if (!optChosenPersonalBonusTile.isPresent()) {
            throw new PersonalBonusTileNotAvailableException();
        }

        PersonalBonusTile chosenPersonalBonusTile = optChosenPersonalBonusTile.get();

        player.setBonusTile(chosenPersonalBonusTile);

        // Remove the chosen bonus tile from the available ones
        getGame().getAvailablePersonalBonusTiles().remove(chosenPersonalBonusTile);

        // Draft next bonus tile
        int currentPlayerIndex = getGame().getPlayers().indexOf(player);
        if (currentPlayerIndex > 0) {
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
    private void startLeaderCardsDraft() {
        setGameState(GameState.DRAFTING_LEADER_CARDS);

        // Draw 4 leader cards for each player
        leaderCardsDraft = new HashMap<>();
        List<LeaderCard> availableLeaderCards = getGame().getAvailableLeaderCards();
        for (Player player : getGame().getPlayers()) {
            List<LeaderCard> playerLeaderCards = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                LeaderCard leaderCard = availableLeaderCards.get(new Random().nextInt(availableLeaderCards.size()));
                playerLeaderCards.add(leaderCard);
                availableLeaderCards.remove(leaderCard);
            }
            leaderCardsDraft.put(player, playerLeaderCards);
        }

        playersThatHaveToDraft = new ArrayList<>(getGame().getPlayers());

        draftNextLeaderCard();
    }

    /**
     * Draft the next leader card
     */
    private void draftNextLeaderCard() {
        // Rotate the available leader cards choices
        List<Player> players = getGame().getPlayers();
        List<LeaderCard> tmp = null;
        for (Player p : players.stream()
                               .limit(players.size() - 1)
                               .collect(Collectors.toList())) {

            int nextPlayerIndex = players.indexOf(p) + 1;
            Player nextPlayer = players.get(nextPlayerIndex);
            tmp = leaderCardsDraft.get(nextPlayer);
            leaderCardsDraft.put(nextPlayer, leaderCardsDraft.get(p));
        }

        // Pass the last player's cards to the first
        Player firstPlayer = players.get(0);
        leaderCardsDraft.put(firstPlayer, tmp);

        // All the players have to draft
        playersThatHaveToDraft = new ArrayList<>(getGame().getPlayers());

        for (Player player : playersThatHaveToDraft) {
            ClientConnection playerConnection = getConnectionForPlayer(player);

            List<LeaderCard> playerLeaderCards = leaderCardsDraft.get(player);

            try {
                playerConnection.askToChooseLeaderCard(playerLeaderCards);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when a player chooses his leader card
     *
     * @param player
     * @param leaderCard
     */
    public void addLeaderCard(Player player, LeaderCard leaderCard) throws ActionNotAllowedException {
        assertGameState(GameState.DRAFTING_LEADER_CARDS);

        // Check that the player has not chosen a leader card yet
        if (!playersThatHaveToDraft.contains(player)) {
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
        if (playersThatHaveToDraft.isEmpty()) {
            // Check if the drafting phase is concluded
            int remainingChoices = leaderCardsDraft.get(player).size();
            if (remainingChoices == 0) {
                // Assign resources to players
                assignInitialResourcesToPlayers();

                // Send game configuration to players
                sendGameConfigurationToPlayers();

                setGameState(GameState.STARTED);

                // Start first round
                startNewRound();
            }
            else {
                // Ask the players to draft the next leader card
                draftNextLeaderCard();
            }
        }
        else {
            try {
                getConnectionForPlayer(player).showWaitingMessage("Waiting for other players to choose...");
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Assign initial resources
     */
    private void assignInitialResourcesToPlayers() {
        for (int i=0; i<getGame().getPlayers().size(); i++) {
            Player player = getGame().getPlayers().get(i);
            player.getResources().put(ObtainableResource.WOOD, 2);
            player.getResources().put(ObtainableResource.STONE, 2);
            player.getResources().put(ObtainableResource.SERVANTS, 3);
            player.getResources().put(ObtainableResource.GOLD, 5 + i);
        }
    }

    /**
     * Send game configuration to the players
     */
    private void sendGameConfigurationToPlayers() {
        getGame().getPlayers().forEach(player -> {
            try {
                getConnectionForPlayer(player).setGameConfiguration(getGame());
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Start new round
     */
    private void startNewRound() {
        updateTurnOrder();
        drawDevelopmentCards();
        throwDice();
        getGame().setCurrentPlayer(getGame().getPlayers().get(0));
        startPlayerTurn(getGame().getPlayers().get(0));
    }

    /**
     * Update the turn order and inform the players
     *
     * Algorithm: scans the players in the council palace *from right to left* and sets them as first,
     * so that the leftmost (the first that has occupied the council palace) will be first in the new turn order.
     */
    private void updateTurnOrder() {
        List<Player> councilPalaceOccupants = getGame().getBoard()
                                                       .getCouncilPalace()
                                                       .getOccupants()
                                                       .stream()
                                                       .map(tuple -> tuple.first)
                                                       .collect(Collectors.toList());

        for(int i = councilPalaceOccupants.size() - 1; i >= 0; i--){
            getGame().setFirstPlayer(councilPalaceOccupants.get(i));
        }

        try {
            for (ClientConnection connection : connections) {
                connection.onTurnOrderChanged(getGame().getPlayers());
            }
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draw development cards and inform the players
     *
     * Place 4 random cards of the appropriate era in each tower
     */
    private void drawDevelopmentCards() {
        Stream<TerritoryCard> currentPeriodTerritoryCards = getGame().getAvailableTerritoryCards()
                                                                     .stream()
                                                                     .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<TerritoryCard> territoryCards = StreamUtils.takeRandomElements(currentPeriodTerritoryCards,4)
                                                        .collect(Collectors.toList());


        Stream<CharacterCard> currentPeriodCharacterCards = getGame().getAvailableCharacterCards()
                                                                     .stream()
                                                                     .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<CharacterCard> characterCards = StreamUtils.takeRandomElements(currentPeriodCharacterCards,4)
                                                        .collect(Collectors.toList());


        Stream<BuildingCard> currentPeriodBuildingCards = getGame().getAvailableBuildingCards()
                                                                   .stream()
                                                                   .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<BuildingCard> buildingCards = StreamUtils.takeRandomElements(currentPeriodBuildingCards,4)
                                                        .collect(Collectors.toList());


        Stream<VentureCard> currentPeriodVentureCards = getGame().getAvailableVentureCards()
                                                                   .stream()
                                                                   .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<VentureCard> ventureCards = StreamUtils.takeRandomElements(currentPeriodVentureCards,4)
                                                        .collect(Collectors.toList());

        gameController.setDevelopmentCards(territoryCards, characterCards, buildingCards, ventureCards);

        for(ClientConnection c : connections) {
            try {
                c.onCardsDrawn(territoryCards, characterCards, buildingCards, ventureCards);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Throws the dice and informs the players
     */
    private void throwDice() {
        Random r = new Random();
        int blackDie = r.nextInt(6) + 1;
        int whiteDie = r.nextInt(6) + 1;
        int orangeDie = r.nextInt(6) + 1;

        gameController.setDiceValues(blackDie, whiteDie, orangeDie);

        for(ClientConnection c : connections) {
            try {
                c.onDiceThrown(blackDie, whiteDie, orangeDie);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Signals to all players that the turn is changed
     * @param player
     */
    private void startPlayerTurn(Player player) {
        setGameState(GameState.PLAYER_TURN);
        for (ClientConnection connection : connections) {
            try {
                connection.onPlayerTurnStarted(player);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when a player goes to an action space
     *
     * @param player
     * @param actionSpace
     * @param familyMemberColor
     * @throws ActionNotAllowedException
     */
    public void placeFamilyMember(Player player, FamilyMemberColor familyMemberColor, ActionSpace actionSpace) throws ActionNotAllowedException {
        ActionSpace serverSideActionSpace = getGame().getBoard().getActionSpaceById(actionSpace.getId());

        // Try to place the family member
        // The controller will throw an exception if the action is not allowed
        gameController.placeFamilyMember(player, familyMemberColor, serverSideActionSpace);

        // Inform everybody
        connections.forEach(connection -> {
            try {
                connection.onPlayerOccupiesActionSpace(player, familyMemberColor, actionSpace);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        // Ask the player to perform the next action
        if(serverSideActionSpace instanceof SmallHarvestArea || serverSideActionSpace instanceof BigHarvestArea) {
            // Perform harvest
            setGameState(GameState.HARVEST);
        }
        else if (serverSideActionSpace instanceof SmallProductionArea || serverSideActionSpace instanceof BigProductionArea) {
            // Perform production
            setGameState(GameState.PRODUCTION);
        }
        else if (serverSideActionSpace instanceof CouncilPalace) {
            // Ask the player what council privileges he wants
            setGameState(GameState.CHOOSING_COUNCIL_PRIVILEGES);
        }
        else if (serverSideActionSpace instanceof Floor) {

        }
    }

    /**
     * @return true if the room can be joined
     */
    public boolean isJoinable() {
        return (gameController.getGameState() == GameState.WAITING_FOR_PLAYERS_TO_CONNECT && connections.size() < 4);
    }

    /**
     * Adds a player connection to the room, starting the room timeout if
     * there are 2 players connected
     *
     * @param clientConnection
     */
    public void addPlayer(ClientConnection clientConnection) throws RoomNotJoinableException {
        if(!isJoinable()){
            throw new RoomNotJoinableException();
        }

        connections.add(clientConnection);
        if (connections.size() == 2) {
            LOGGER.info("Starting room timeout");
            new Thread(new RoomTimerClass()).start();
        }
    }

    /**
     * Asserts that the game is in a certain state or throw an ActionNotAllowedException
     * @param gameState
     * @throws ActionNotAllowedException
     */
    private void assertGameState(GameState gameState) throws ActionNotAllowedException {
        if (gameController.getGameState() != gameState) {
            throw new ActionNotAllowedException();
        }
    }

    /**
     * Get the connection to the specified player
     * @param player
     * @return
     */
    private ClientConnection getConnectionForPlayer(Player player) {
        Optional<ClientConnection> connection = connections.stream()
                                                           .filter(p -> p.getPlayer().equals(player))
                                                           .findFirst();
        if (connection.isPresent()) {
            return connection.get();
        }
        else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Utility method to get the game from the game controller
     * @return
     */
    private Game getGame() {
        return gameController.getGame();
    }

    private class RoomTimerClass implements Runnable {
        @Override
        @SuppressWarnings("squid:S2142") // Suppress "InterruptedException should not be ignored" warning
        public void run() {
            // The time when the timer was started, needed to handle exceptions
            long startTime;
            long remainingTime = gameStartTimeout;

            while (true) {
                startTime = System.currentTimeMillis();
                try {
                    Thread.sleep(remainingTime);
                    startGame();
                    break;
                }
                catch (InterruptedException e) {
                    // If somehow the thread is woken up early compute the
                    // remaining time and go back to sleep again
                    remainingTime = remainingTime - (System.currentTimeMillis() - startTime);
                }
            }
        }
    }
}
