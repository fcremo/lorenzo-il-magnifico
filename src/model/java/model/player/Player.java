package model.player;

import model.Excommunication;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.card.development.DevelopmentCard;
import model.card.leader.LeaderCard;
import model.resource.ObtainableResource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents the player state
 */
public class Player {
    /**
     * The servants spent to increase the value of the next action
     */
    int spentServants;
    private String username;
    private PlayerColor color;
    private HashMap<ObtainableResource, Integer> resources = new HashMap<>();
    /**
     * The available (not already played) leader cards
     */
    private ArrayList<LeaderCard> availableLeaderCards;
    /**
     * The played leader cards
     */
    private ArrayList<LeaderCard> playedLeaderCards;
    private ArrayList<DevelopmentCard> territories;
    private ArrayList<DevelopmentCard> ventures;
    private ArrayList<DevelopmentCard> buildings;
    private ArrayList<DevelopmentCard> characters;
    /**
     * The available (not placed in this turn) family members
     */
    private HashSet<FamilyMemberColor> availableFamilyMembers = new HashSet<>();
    /**
     * The player bonus tile
     */
    private PersonalBonusTile bonusTile;

    /**
     * The excommunications
     */
    private ArrayList<Excommunication> excommunications;

    public Player(String username) {
        this.username = username;
    }

    /**
     * Returns the family member value for the specified action, accounting for personal bonuses and maluses due to
     * the effects of leader cards, excommunications or development cards.
     *
     * @param color  the family member color
     * @param action the action to perform
     * @return the family member value
     */
    public int getFamilyMemberValue(FamilyMemberColor color, ActionSpace actionSpace) {
        // TODO: 5/7/17
        throw new NotImplementedException();
    }

    public int getActionValue(Action action) {
        // TODO: 5/7/17
        throw new NotImplementedException();
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<LeaderCard> getAvailableLeaderCards() {
        return availableLeaderCards;
    }

    public void setAvailableLeaderCards(ArrayList<LeaderCard> availableLeaderCards) {
        this.availableLeaderCards = availableLeaderCards;
    }

    public ArrayList<LeaderCard> getPlayedLeaderCards() {
        return playedLeaderCards;
    }

    public void setPlayedLeaderCards(ArrayList<LeaderCard> playedLeaderCards) {
        this.playedLeaderCards = playedLeaderCards;
    }

    public ArrayList<DevelopmentCard> getTerritories() {
        return territories;
    }

    public void setTerritories(ArrayList<DevelopmentCard> territories) {
        this.territories = territories;
    }

    public ArrayList<DevelopmentCard> getVentures() {
        return ventures;
    }

    public void setVentures(ArrayList<DevelopmentCard> ventures) {
        this.ventures = ventures;
    }

    public ArrayList<DevelopmentCard> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<DevelopmentCard> buildings) {
        this.buildings = buildings;
    }

    public ArrayList<DevelopmentCard> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<DevelopmentCard> characters) {
        this.characters = characters;
    }

    public PersonalBonusTile getBonusTile() {
        return bonusTile;
    }

    public void setBonusTile(PersonalBonusTile bonusTile) {
        this.bonusTile = bonusTile;
    }

    public ArrayList<Excommunication> getExcommunications() {
        return excommunications;
    }

    public void setExcommunications(ArrayList<Excommunication> excommunications) {
        this.excommunications = excommunications;
    }

    public int getSpentServants() {
        return spentServants;
    }

    public void setSpentServants(int spentServants) {
        this.spentServants = spentServants;
    }
}
