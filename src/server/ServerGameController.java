package server;

import client.exceptions.NetworkException;
import gamecontroller.GameController;
import gamecontroller.GameState;
import gamecontroller.exceptions.ActionNotAllowedException;
import gamecontroller.exceptions.PlayerDoesNotExistException;
import gamecontroller.utils.StreamUtils;
import model.Excommunication;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
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
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.configloader.ConfigLoader;
import server.exceptions.GameNotJoinableException;
import server.exceptions.LeaderCardNotAvailableException;
import server.exceptions.PersonalBonusTileNotAvailableException;

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
     * The time to wait (in milliseconds) before starting the game after two players have joined.
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

    /* ----------------------------------------------------------------
     * PRIVATE METHODS THAT CONTROL THE FLOW OF THE GAME
     * These methods do server-specific actions
     * (loading configuration, coordinating drafts, throwing the dice, and so on)
     * ---------------------------------------------------------------- */
    /**
     * Callback called when the game timeout expires or 4 players have joined
     */
    private void startGame() {
        // Ignore repeated calls (made if 4 players connect before the timeout expires)
        if(gameController.getGameState() != GameState.WAITING_FOR_PLAYERS_TO_CONNECT) return;

        gameController.setGameState(GameState.STARTED);

        LOGGER.info("Starting game...");
        // Load game configuration
        loadConfiguration();

        // Create players and set colors
        createPlayers();

        // Extract excommunications
        drawExcommunications();

        // Set random turn order
        shufflePlayers();

        // Start personal bonus tile draft phase (in reverse turn order)
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
        if (connections.size() < 4) {
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

            // Add player to the game
            getGame().addPlayer(player);

            i++;
        }
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
     * Set a random player order and update current player
     */
    private void shufflePlayers() {
        Collections.shuffle(getGame().getPlayers());
    }

    /**
     * Start the personal bonus tile draft
     */
    private void startPersonalBonusTileDraft() {
        gameController.setGameState(GameState.DRAFTING_BONUS_TILES);
        Player nextPlayer = getGame().getPlayers().get(getGame().getPlayers().size() - 1);
        getGame().setCurrentPlayer(nextPlayer);
        draftNextBonusTile();
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
            handleRemoteException(e);
        }

        // Put the others to wait
        getGame().getPlayers().stream()
                 .filter(player -> !player.equals(getGame().getCurrentPlayer()))
                 .forEach(player -> {
                     try {
                         getConnectionForPlayer(player).showWaitingMessage("Wait for other players to choose a bonus tile");
                     }
                     catch (RemoteException e) {
                         handleRemoteException(e);
                     }
                 });
    }

    /**
     * Start drafting the leader cards
     */
    private void startLeaderCardsDraft() {
        gameController.setGameState(GameState.DRAFTING_LEADER_CARDS);

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
                handleRemoteException(e);
            }
        }
    }

    /**
     * Called at the end of the leader cards draft
     */
    private void leaderCardsDraftConcluded() {
        // Assign resources to players
        assignInitialResourcesToPlayers();

        // Send game configuration to players
        sendGameConfigurationToPlayers();

        // Start first round
        startNewRound();
    }

    /**
     * Assign initial resources
     */
    private void assignInitialResourcesToPlayers() {
        for (int i = 0; i < getGame().getPlayers().size(); i++) {
            Player player = getGame().getPlayers().get(i);
            player.getResources().addResource(ObtainableResource.WOOD, 2);
            player.getResources().addResource(ObtainableResource.STONE, 2);
            player.getResources().addResource(ObtainableResource.SERVANTS, 3);
            player.getResources().addResource(ObtainableResource.GOLD, 5 + i);
        }
    }

    /**
     * Send game configuration to the players
     */
    private void sendGameConfigurationToPlayers() {
        getGame().getPlayers().stream().forEach(player -> {
            try {
                getConnectionForPlayer(player).setGameConfiguration(getGame());
            }
            catch (RemoteException e) {
                handleRemoteException(e);
            }
        });
    }

    /**
     * Start new round
     */
    private void startNewRound() {
        gameController.prepareNewRound();

        connections.forEach(connection -> {
            try {
                connection.onPrepareNewRound();
            }
            catch (RemoteException e) {
                handleRemoteException(e);
            }
        });

        drawDevelopmentCards();

        throwDice();

        startPlayerTurn(getGame().getPlayers().get(0));
    }

    /**
     * Draw development cards and inform the players
     * <p>
     * Place 4 random cards of the appropriate era in each tower
     */
    private void drawDevelopmentCards() {
        Stream<TerritoryCard> currentPeriodTerritoryCards = getGame().getAvailableTerritoryCards()
                                                                     .stream()
                                                                     .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<UUID> territoryCardsIds = StreamUtils.takeRandomElements(currentPeriodTerritoryCards, 4)
                                                  .map(card -> card.getId())
                                                  .collect(Collectors.toList());


        Stream<CharacterCard> currentPeriodCharacterCards = getGame().getAvailableCharacterCards()
                                                                     .stream()
                                                                     .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<UUID> characterCardsIds = StreamUtils.takeRandomElements(currentPeriodCharacterCards, 4)
                                                  .map(card -> card.getId())
                                                  .collect(Collectors.toList());


        Stream<BuildingCard> currentPeriodBuildingCards = getGame().getAvailableBuildingCards()
                                                                   .stream()
                                                                   .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<UUID> buildingCardsIds = StreamUtils.takeRandomElements(currentPeriodBuildingCards, 4)
                                                      .map(card -> card.getId())
                                                      .collect(Collectors.toList());


        Stream<VentureCard> currentPeriodVentureCards = getGame().getAvailableVentureCards()
                                                                 .stream()
                                                                 .filter(card -> card.getPeriod() == (getGame().getCurrentPeriod()));

        List<UUID> ventureCardsIds = StreamUtils.takeRandomElements(currentPeriodVentureCards, 4)
                                                    .map(card -> card.getId())
                                                    .collect(Collectors.toList());

        try {
            gameController.setDevelopmentCards(territoryCardsIds, characterCardsIds, buildingCardsIds, ventureCardsIds);
        }
        catch (ActionNotAllowedException e) {
            // This should never happen
            e.printStackTrace();
        }

        for (ClientConnection c : connections) {
            try {
                c.onCardsDrawn(territoryCardsIds, characterCardsIds, buildingCardsIds, ventureCardsIds);
            }
            catch (RemoteException e) {
                handleRemoteException(e);
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

        for (ClientConnection c : connections) {
            try {
                c.onDiceThrown(blackDie, whiteDie, orangeDie);
            }
            catch (RemoteException e) {
                handleRemoteException(e);
            }
        }
    }

    /**
     * Signals to all players that the turn is changed
     *
     * @param player
     */
    private void startPlayerTurn(Player player) {
        try {
            gameController.startPlayerTurn(player.getUsername());
        }
        catch (PlayerDoesNotExistException e) {
            e.printStackTrace(); // This should never happen
        }

        for (ClientConnection connection : connections) {
            try {
                connection.onPlayerTurnStarted(player.getUsername());
            }
            catch (RemoteException e) {
                handleRemoteException(e);
            }
        }
    }

    /* ----------------------------------------------------------------
     * PUBLIC METHODS THAT ARE CALLED WHEN THE PLAYERS DO SOMETHING
     * These methods forward the actions to the game controller
     * (which validates them and modifies the server side game state)
     * and then inform all the players, so that they can update
     * the game state for themselves.
     *
     * The methods related to the drafts bypass the game controller
     * and do not forward the actions to the players since
     * the draft choices should be secret.
     * This is not a problem since after the draft the full game
     * configuration is sent to the players.
     *
     * ---------------------------------------------------------------- */

    /**
     * Called when a player chooses his personal bonus tile
     * TODO: keep track of whick players have drafted like in the leader cards draft
     *
     * @param username
     * @param personalBonusTile
     */
    public void choosePersonalBonusTile(String username, UUID personalBonusTileId) throws ActionNotAllowedException {
        gameController.setPersonalBonusTile(username, personalBonusTileId);

        Player player = gameController.getLocalPlayer(username);

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
     * Called when a player chooses his leader card
     *
     * @param username
     * @param leaderCard
     */
    public void chooseLeaderCard(String username, UUID leaderCardId) throws ActionNotAllowedException {
        Player player = gameController.getLocalPlayer(username);

        gameController.assertGameState(GameState.DRAFTING_LEADER_CARDS);

        // Check that the player has not chosen a leader card yet
        if (!playersThatHaveToDraft.contains(player)) {
            throw new ActionNotAllowedException("You have already chosen a leader card");
        }

        // Check that the player could choose this leader card
        Optional<LeaderCard> optChosenLeaderCard = leaderCardsDraft.get(player).stream()
                                                                   .filter(card -> card.getId().equals(leaderCardId))
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
                leaderCardsDraftConcluded();
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
                handleRemoteException(e);
            }
        }
    }

    /**
     * Called when a player goes to a floor
     *
     * @param username
     * @param floor
     * @param familyMember
     * @param paymentForCard
     * @throws NetworkException
     * @throws RemoteException
     * @throws ActionNotAllowedException
     */
    public void goToFloor(String username,
                          UUID floorId,
                          FamilyMemberColor familyMember,
                          RequiredResourceSet paymentForCard,
                          List<ObtainableResourceSet> councilPrivileges) throws ActionNotAllowedException {
        // Try to place the family member
        // The controller will throw an exception if the action is not allowed
        gameController.goToFloor(username, familyMember, floorId, paymentForCard, councilPrivileges);

        // Inform all players
        connections.forEach(connection -> {
            try {
                connection.onPlayerOccupiesFloor(username, floorId, familyMember, councilPrivileges, paymentForCard);
            }
            catch (RemoteException e) {
                handleRemoteException(e);
            }
        });
    }

    /**
     * Called when a player wants to occupy an action space
     * (except a floor for which there's a dedicated method)
     *
     * @param username the username of the player
     * @param familyMemberColor the family member he wants to use
     * @param chosenPrivileges the council privileges the player has chosen
     */
    public void goToActionSpace(String username, ActionSpace actionSpace, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws ActionNotAllowedException {
        // If the action is not allowed the game controller will throw an exception
        gameController.goToActionSpace(username, actionSpace, familyMemberColor, chosenPrivileges);

        // Inform all players
        connections.forEach(connection -> {
            try {
                connection.onPlayerOccupiesActionSpace(username, actionSpace, familyMemberColor, chosenPrivileges);
            }
            catch (RemoteException e) {
                handleRemoteException(e);
            }
        });
    }

    public void spendServants(String username, int servants) throws ActionNotAllowedException {
        gameController.spendServants(username, servants);

        connections.stream().forEach(
                connection -> {
                    try {
                        connection.onPlayerSpendsServants(username, servants);
                    }
                    catch (RemoteException e) {
                        handleRemoteException(e);
                    }
        });
    }

    public void endTurn(String username) throws ActionNotAllowedException {
        gameController.assertGameState(GameState.PLAYER_TURN);
        Player player = gameController.getLocalPlayer(username);
        gameController.assertPlayerTurn(player);

        List<Player> turnOrder = getGame().getPlayers();

        // If the player is last in turn order, start next round
        int playerIndex = turnOrder.indexOf(player);
        if(playerIndex == turnOrder.size() - 1) {
            // TODO
        }
        // else start next player's turn
        else {
            Player nextPlayer = turnOrder.get(playerIndex + 1);
            startPlayerTurn(nextPlayer);
        }

    }

    /* ----------------------------------------------------------------
     * Methods related to the player connections, joining, exceptions
     * ---------------------------------------------------------------- */

    /**
     * @return true if the game can be joined
     */
    public boolean isJoinable() {
        return (gameController.getGameState() == GameState.WAITING_FOR_PLAYERS_TO_CONNECT && connections.size() < 4);
    }

    /**
     * Adds a player connection to the game,
     * starting the timeout if there are 2 players connected
     *
     * @param clientConnection
     */
    public void addPlayer(ClientConnection clientConnection) throws GameNotJoinableException {
        if (!isJoinable()) {
            throw new GameNotJoinableException();
        }

        connections.add(clientConnection);
        if (connections.size() == 2) {
            LOGGER.info("Starting game timeout");
            new Thread(new GameStartTimeout()).start();
        }
        if (connections.size() == 4) {
            startGame();

            // startGame ignores repeated calls
            // not a problem if we don't kill the timeout thread
        }
    }

    public List<ClientConnection> getConnections() {
        return connections;
    }

    /**
     * Get the connection to the specified player
     *
     * @param player
     * @return
     */
    private ClientConnection getConnectionForPlayer(Player player) {
        Optional<ClientConnection> connection = connections.stream()
                                                           .filter(c -> c.getUsername().equals(player.getUsername()))
                                                           .findFirst();
        if (connection.isPresent()) {
            return connection.get();
        }
        else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Handle a remote exception from a player connection
     */
    @SuppressWarnings("squid:S1166") // Suppress "Log or rethrow this exception"
    private void handleRemoteException(RemoteException e) {
        connections.forEach(connection -> {
            try {
                connection.abortGame("One of the clients crashed. Cannot go on.");
            }
            catch (RemoteException e1) {
                // We're aborting the game, nothing to do anyway
            }
        });
    }

    /**
     * Utility method to get the game from the game controller
     *
     * @return
     */
    private Game getGame() {
        return gameController.getGame();
    }

    private class GameStartTimeout implements Runnable {
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
