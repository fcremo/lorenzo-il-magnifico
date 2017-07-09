package model;

import model.board.Board;
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
import model.resource.ObtainableResourceSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the root object representing the state of the game
 */
public class Game implements Serializable {
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
    private ArrayList<ObtainableResourceSet> councilPrivileges;

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
    private int blackDie;

    /**
     * The value of the white dice as rolled (does not account for bonuses/maluses)
     */
    private int whiteDie;

    /**
     * The value of the orange dice as rolled (does not account for bonuses/maluses)
     */
    private int orangeDie;

    public Game() {
        board = new Board(this);
    }

    /**
     * Sets the player as first in the turn order
     *
     * @param player the player that's going to be first
     */
    public void setFirstPlayer(Player player) {
        this.players.remove(player);
        this.players.add(0, player);
    }

    public void addPlayer(Player player) throws IllegalStateException {
        if (this.isStarted) throw new IllegalStateException("Game is already started, can't add a new player");

        this.players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
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

    public int getCurrentPeriod() {
        return ((int) Math.ceil(((float) currentRound) / 2));
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
    public int getBlackDie() {
        return blackDie;
    }

    public void setBlackDie(int blackDice) {
        this.blackDie = blackDice;
    }

    /**
     * Returns the value on the face of the white dice.
     * Does not account for bonuses/maluses.
     *
     * @returns the white dice value
     */
    public int getWhiteDie() {
        return whiteDie;
    }

    public void setWhiteDie(int whiteDice) {
        this.whiteDie = whiteDice;
    }

    /**
     * Returns the value on the face of the orange dice.
     * Does not account for bonuses/maluses.
     *
     * @returns the orange dice value
     */
    public int getOrangeDie() {
        return orangeDie;
    }

    public void setOrangeDie(int orangeDice) {
        this.orangeDie = orangeDice;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<TerritoryCard> getAvailableTerritoryCards() {
        return availableTerritoryCards;
    }

    public void setAvailableTerritoryCards(List<TerritoryCard> availableTerritoryCards) {
        this.availableTerritoryCards = new ArrayList<>(availableTerritoryCards);
    }

    public List<BuildingCard> getAvailableBuildingCards() {
        return availableBuildingCards;
    }

    public void setAvailableBuildingCards(List<BuildingCard> availableBuildingCards) {
        this.availableBuildingCards = new ArrayList<>(availableBuildingCards);
    }

    public List<CharacterCard> getAvailableCharacterCards() {
        return availableCharacterCards;
    }

    public void setAvailableCharacterCards(List<CharacterCard> availableCharacterCards) {
        this.availableCharacterCards = new ArrayList<>(availableCharacterCards);
    }

    public List<VentureCard> getAvailableVentureCards() {
        return availableVentureCards;
    }

    public void setAvailableVentureCards(List<VentureCard> availableVentureCards) {
        this.availableVentureCards = new ArrayList<>(availableVentureCards);
    }

    public List<Excommunication> getAvailableExcommunications() {
        return availableExcommunications;
    }

    public void setAvailableExcommunications(List<Excommunication> availableExcommunications) {
        this.availableExcommunications = new ArrayList<>(availableExcommunications);
    }

    public List<PersonalBonusTile> getAvailablePersonalBonusTiles() {
        return availablePersonalBonusTiles;
    }

    public void setAvailablePersonalBonusTiles(List<PersonalBonusTile> availablePersonalBonusTiles) {
        this.availablePersonalBonusTiles = new ArrayList<>(availablePersonalBonusTiles);
    }

    public List<LeaderCard> getAvailableLeaderCards() {
        return availableLeaderCards;
    }

    public void setAvailableLeaderCards(List<LeaderCard> availableLeaderCards) {
        this.availableLeaderCards = new ArrayList<>(availableLeaderCards);
    }

    public List<ObtainableResourceSet> getAllowedCouncilPrivileges() {
        return councilPrivileges;
    }

    public void setCouncilPrivileges(List<ObtainableResourceSet> councilPrivileges) {
        this.councilPrivileges = new ArrayList<>(councilPrivileges);
    }

    public int getInitialValueForFamilyMember(FamilyMemberColor familyMember) {
        if(familyMember == FamilyMemberColor.BLACK) return blackDie;
        else if(familyMember == FamilyMemberColor.ORANGE) return orangeDie;
        else if(familyMember == FamilyMemberColor.WHITE) return whiteDie;
        else return 0;
    }

    public List<Floor> getFloors() {
        List<Floor> floors = new ArrayList<>();

        floors.addAll(board.getTerritoryTower().getFloors());
        floors.addAll(board.getBuildingTower().getFloors());
        floors.addAll(board.getCharacterTower().getFloors());
        floors.addAll(board.getVentureTower().getFloors());

        return floors;
    }

    public List<ActionSpace> getActionSpaces() {
        List<ActionSpace> actionSpaces = new ArrayList<>();

        actionSpaces.add(board.getMarket1());
        actionSpaces.add(board.getMarket2());
        actionSpaces.add(board.getMarket3());
        actionSpaces.add(board.getMarket4());
        actionSpaces.add(board.getSmallProductionArea());
        actionSpaces.add(board.getBigProductionArea());
        actionSpaces.add(board.getSmallHarvestArea());
        actionSpaces.add(board.getBigHarvestArea());
        actionSpaces.add(board.getCouncilPalace());

        return actionSpaces;

    }
}