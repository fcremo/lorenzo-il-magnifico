package model;

import model.board.Board;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.leader.LeaderCard;
import model.player.Player;
import model.player.PersonalBonusTile;
import model.resource.ObtainedResourceSet;

import java.util.ArrayList;

/**
 * This is the root object representing the state of the game
 */
public class Game {
    /**
     * The players of the game, *in turn order*
     */
    private ArrayList<Player> players = new ArrayList<>();

    /**
     * The deck of available territory cards
     */
    private ArrayList<TerritoryCard> availableTerritoryCards;

    /**
     * The deck of available building cards
     */
    private ArrayList<BuildingCard> availableBuildingCards;

    /**
     * The deck of available character cards
     */
    private ArrayList<CharacterCard> availableCharacterCards;

    /**
     * The deck of available venture cards
     */
    private ArrayList<VentureCard> availableVentureCards;

    /**
     * The list of available excommunications
     */
    private ArrayList<Excommunication> availableExcommunications;

    /**
     * The list of available personal bonus tiles
     */
    private ArrayList<PersonalBonusTile> availablePersonalBonusTiles;

    /**
     * The list of available leader cards
     */
    private ArrayList<LeaderCard> availableLeaderCards;

    /**
     * The list of possible council privileges the player can choose from
     */
    private ArrayList<ObtainedResourceSet> councilPrivileges;

    /**
     * The Board object contains the board state
     */
    private Board board;

    /**
     * True if the game has started
     */
    private boolean isStarted = false;

    /**
     * True if the game has ended
     */
    private boolean isEnded = false;

    /**
     * The current player
     */
    private Player currentPlayer;

    /**
     * The current round
     */
    private int currentRound = 1;

    /**
     * The value of the black dice as rolled (does not account for bonuses/maluses)
     */
    private int blackDice;

    /**
     * The value of the white dice as rolled (does not account for bonuses/maluses)
     */
    private int whiteDice;

    /**
     * The value of the orange dice as rolled (does not account for bonuses/maluses)
     */
    private int orangeDice;

    public Game() {
        board = new Board(this);
    }

    /**
     * Sets the player as first in the turn order
     *
     * @param player the player that's going to be first
     */
    public void setFirstPlayer(Player player) {
        // TODO: 4/11/17
    }

    public void addPlayer(Player player) throws IllegalStateException {
        if (this.isStarted) throw new IllegalStateException("Game is already started, can't add a new player");

        this.players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players.clear();
        this.players.addAll(players);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Sets the game as started
     *
     * @throws IllegalStateException
     */
    public void setStarted() throws IllegalStateException {
        if (this.players.size() < 2 || this.players.size() > 4) {
            throw new IllegalStateException("Can't start a game with " + Integer.toString(this.players.size()) + "players");
        }
        this.isStarted = true;
    }

    public boolean isEnded() {
        return isEnded;
    }

    /**
     * Sets the game as ended
     *
     * @throws IllegalStateException
     */
    public void setEnded() throws IllegalStateException {
        if (!this.isStarted) {
            throw new IllegalStateException("Can't end a game before it even started!");
        }
        this.isEnded = true;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Increments the round counter
     */
    public void nextRound() {
        this.currentRound++;
    }

    /**
     * Returns the value on the face of the black dice.
     * Does not account for bonuses/maluses.
     *
     * @returns the black dice value
     */
    public int getBlackDice() {
        return blackDice;
    }

    public void setBlackDice(int blackDice) {
        this.blackDice = blackDice;
    }

    /**
     * Returns the value on the face of the white dice.
     * Does not account for bonuses/maluses.
     *
     * @returns the white dice value
     */
    public int getWhiteDice() {
        return whiteDice;
    }

    public void setWhiteDice(int whiteDice) {
        this.whiteDice = whiteDice;
    }

    /**
     * Returns the value on the face of the orange dice.
     * Does not account for bonuses/maluses.
     *
     * @returns the orange dice value
     */
    public int getOrangeDice() {
        return orangeDice;
    }

    public void setOrangeDice(int orangeDice) {
        this.orangeDice = orangeDice;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public ArrayList<TerritoryCard> getAvailableTerritoryCards() {
        return availableTerritoryCards;
    }

    public void setAvailableTerritoryCards(ArrayList<TerritoryCard> availableTerritoryCards) {
        this.availableTerritoryCards = availableTerritoryCards;
    }

    public ArrayList<BuildingCard> getAvailableBuildingCards() {
        return availableBuildingCards;
    }

    public void setAvailableBuildingCards(ArrayList<BuildingCard> availableBuildingCards) {
        this.availableBuildingCards = availableBuildingCards;
    }

    public ArrayList<CharacterCard> getAvailableCharacterCards() {
        return availableCharacterCards;
    }

    public void setAvailableCharacterCards(ArrayList<CharacterCard> availableCharacterCards) {
        this.availableCharacterCards = availableCharacterCards;
    }

    public ArrayList<VentureCard> getAvailableVentureCards() {
        return availableVentureCards;
    }

    public void setAvailableVentureCards(ArrayList<VentureCard> availableVentureCards) {
        this.availableVentureCards = availableVentureCards;
    }

    public ArrayList<Excommunication> getAvailableExcommunications() {
        return availableExcommunications;
    }

    public void setAvailableExcommunications(ArrayList<Excommunication> availableExcommunications) {
        this.availableExcommunications = availableExcommunications;
    }

    public ArrayList<PersonalBonusTile> getAvailablePersonalBonusTiles() {
        return availablePersonalBonusTiles;
    }

    public void setAvailablePersonalBonusTiles(ArrayList<PersonalBonusTile> availablePersonalBonusTiles) {
        this.availablePersonalBonusTiles = availablePersonalBonusTiles;
    }

    public ArrayList<LeaderCard> getAvailableLeaderCards() {
        return availableLeaderCards;
    }

    public void setAvailableLeaderCards(ArrayList<LeaderCard> availableLeaderCards) {
        this.availableLeaderCards = availableLeaderCards;
    }

    public ArrayList<ObtainedResourceSet> getCouncilPrivileges() {
        return councilPrivileges;
    }

    public void setCouncilPrivileges(ArrayList<ObtainedResourceSet> councilPrivileges) {
        this.councilPrivileges = councilPrivileges;
    }
}