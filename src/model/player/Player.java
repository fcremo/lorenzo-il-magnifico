package model.player;

import model.Excommunication;
import model.action.Action;
import model.board.actionspace.ActionSpace;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.effects.ObtainedResourceSetModifierEffect;
import model.card.effects.interfaces.EffectInterface;
import model.card.leader.LeaderCard;
import model.resource.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.*;

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

    private ObtainedResourceSet resources = new ObtainedResourceSet(this);

    /**
     * The available (not already played) leader cards
     */
    private List<LeaderCard> availableLeaderCards = new ArrayList<>();

    /**
     * The played leader cards
     */
    private List<LeaderCard> playedLeaderCards = new ArrayList<>();

    private List<TerritoryCard> territories = new ArrayList<>();
    private List<VentureCard> ventures = new ArrayList<>();
    private List<BuildingCard> buildings = new ArrayList<>();
    private List<CharacterCard> characters = new ArrayList<>();
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
    private List<Excommunication> excommunications = new ArrayList<>();

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

    public List<TerritoryCard> getTerritories() {
        return territories;
    }

    public void setTerritories(List<TerritoryCard> territories) {
        this.territories = new ArrayList<>(territories);
    }

    public List<VentureCard> getVentures() {
        return ventures;
    }

    public void setVentures(List<VentureCard> ventures) {
        this.ventures = new ArrayList<>(ventures);
    }

    public List<BuildingCard> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<BuildingCard> buildings) {
        this.buildings = new ArrayList<>(buildings);
    }

    public List<CharacterCard> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterCard> characters) {
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

    public ObtainedResourceSet getResources() {
        return resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return username.equals(player.username);
    }

    /**
     * Shortcut to get all the effects for the player
     * @return all the effects for this player
     */
    public List<EffectInterface> getAllEffects() {
        List<EffectInterface> effects = new ArrayList<>();
        excommunications.forEach(excommunication -> effects.addAll(excommunication.getEffectsContainer().getEffects()));
        playedLeaderCards.forEach(card -> effects.addAll(card.getEffectsContainer().getEffects()));
        territories.forEach(card -> effects.addAll(card.getEffectsContainer().getEffects()));
        characters.forEach(card -> effects.addAll(card.getEffectsContainer().getEffects()));
        buildings.forEach(card -> effects.addAll(card.getEffectsContainer().getEffects()));
        ventures.forEach(card -> effects.addAll(card.getEffectsContainer().getEffects()));
        return effects;
    }

    /**
     * Shortcut to get all the effects implementing a given interface
     * @param effectInterface
     * @param <T>
     * @return
     */
    public <T extends EffectInterface> List<T> getEffectsImplementing(Class<T> effectInterface) {
        List<T> effects = new ArrayList<>();
        excommunications.forEach(excommunication -> effects.addAll(excommunication.getEffectsContainer().getEffectsImplementing(effectInterface)));
        playedLeaderCards.forEach(card -> effects.addAll(card.getEffectsContainer().getEffectsImplementing(effectInterface)));
        territories.forEach(card -> effects.addAll(card.getEffectsContainer().getEffectsImplementing(effectInterface)));
        characters.forEach(card -> effects.addAll(card.getEffectsContainer().getEffectsImplementing(effectInterface)));
        buildings.forEach(card -> effects.addAll(card.getEffectsContainer().getEffectsImplementing(effectInterface)));
        ventures.forEach(card -> effects.addAll(card.getEffectsContainer().getEffectsImplementing(effectInterface)));
        return effects;
    }

    /**
     * Shortcut method that returns true if the player has at least one effect implementing the given interface
     *
     * @param effectInterface
     * @return
     */
    public boolean hasEffectsImplementing(Class<? extends EffectInterface> effectInterface) {
        return !getEffectsImplementing(effectInterface).isEmpty();
    }

    /**
     * Utility method that checks if the player has enough resources to cover the given requirements
     * @param requirements
     * @return
     */
    public boolean hasEnoughResources(RequiredResourceSet requirements) {
        return resources.has(requirements);
    }

    /**
     * Utility method that adds a resource set to the player
     * @param obtainableResourceSet
     */
    public void addResources(ObtainableResourceSet obtainableResourceSet) {
        resources.addResources(obtainableResourceSet);
    }

    /**
     * Utility method that adds resources to the player
     * @param obtainableResourceSets
     */
    public void addResources(List<ObtainableResourceSet> obtainableResourceSets) {
        obtainableResourceSets.forEach(this::addResources);
    }

    public Set<FamilyMemberColor> getAvailableFamilyMembers() {
        return availableFamilyMembers;
    }

    public void useFamilyMember(FamilyMemberColor familyMemberColor) {
        availableFamilyMembers.remove(familyMemberColor);
    }


    public void resetAvailableFamilyMembers() {
        availableFamilyMembers.clear();
        availableFamilyMembers.add(FamilyMemberColor.BLACK);
        availableFamilyMembers.add(FamilyMemberColor.WHITE);
        availableFamilyMembers.add(FamilyMemberColor.ORANGE);
        availableFamilyMembers.add(FamilyMemberColor.NEUTRAL);
    }
}