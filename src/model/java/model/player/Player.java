package model.player;

import model.Excommunication;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.card.development.DevelopmentCard;
import model.card.leader.LeaderCard;
import model.resource.ObtainableResource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * This class represents the player state
 */
public class Player implements Serializable {
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
    private ArrayList<LeaderCard> availableLeaderCards = new ArrayList<>();
    /**
     * The played leader cards
     */
    private ArrayList<LeaderCard> playedLeaderCards = new ArrayList<>();
    private ArrayList<DevelopmentCard> territories = new ArrayList<>();
    private ArrayList<DevelopmentCard> ventures = new ArrayList<>();
    private ArrayList<DevelopmentCard> buildings = new ArrayList<>();
    private ArrayList<DevelopmentCard> characters = new ArrayList<>();
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
    private ArrayList<Excommunication> excommunications = new ArrayList<>();

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

    public List<LeaderCard> getAvailableLeaderCards() {
        return availableLeaderCards;
    }

    public void setAvailableLeaderCards(List<LeaderCard> availableLeaderCards) {
        this.availableLeaderCards = new ArrayList<>(availableLeaderCards);
    }

    public List<LeaderCard> getPlayedLeaderCards() {
        return playedLeaderCards;
    }

    public void setPlayedLeaderCards(List<LeaderCard> playedLeaderCards) {
        this.playedLeaderCards = new ArrayList<>(playedLeaderCards);
    }

    public List<DevelopmentCard> getTerritories() {
        return territories;
    }

    public void setTerritories(List<DevelopmentCard> territories) {
        this.territories = new ArrayList<>(territories);
    }

    public List<DevelopmentCard> getVentures() {
        return ventures;
    }

    public void setVentures(List<DevelopmentCard> ventures) {
        this.ventures = new ArrayList<>(ventures);
    }

    public List<DevelopmentCard> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<DevelopmentCard> buildings) {
        this.buildings = new ArrayList<>(buildings);
    }

    public List<DevelopmentCard> getCharacters() {
        return characters;
    }

    public void setCharacters(List<DevelopmentCard> characters) {
        this.characters = new ArrayList<>(characters);
    }

    public PersonalBonusTile getBonusTile() {
        return bonusTile;
    }

    public void setBonusTile(PersonalBonusTile bonusTile) {
        this.bonusTile = bonusTile;
    }

    public List<Excommunication> getExcommunications() {
        return excommunications;
    }

    public void setExcommunications(List<Excommunication> excommunications) {
        this.excommunications = new ArrayList<>(excommunications);
    }

    public int getSpentServants() {
        return spentServants;
    }

    public void setSpentServants(int spentServants) {
        this.spentServants = spentServants;
    }

    public HashMap<ObtainableResource, Integer> getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return username.equals(player.username);
    }
}
