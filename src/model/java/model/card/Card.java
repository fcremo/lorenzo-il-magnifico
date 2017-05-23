package model.card;

import model.card.effects.EffectsContainer;
import model.resource.RequiredResourceSet;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class representing a leader or development card
 */
public class Card {
    /**
     * The unique id of the card
     */
    private String id;

    /**
     * The name of the card
     */
    private String name;

    /**
     * The resource set needed to take/play the card
     */
    private ArrayList<RequiredResourceSet> requiredResourceSets;

    /**
     * The effects of the card
     */
    private EffectsContainer effects;

    public Card(String id, String name, List<RequiredResourceSet> requiredResourceSets) {
        this.id = id;
        this.name = name;
        this.requiredResourceSets = new ArrayList<>(requiredResourceSets);
    }

    /**
     * Returns a list of resources that the player can pay to get the card.
     * Only one of them is required (e.g. 2 gold OR (1 wood and 1 stone))
     *
     * @return the list of resources
     */
    public List<RequiredResourceSet> getRequiredResourceSets() {
        return requiredResourceSets;
    }

    public void setRequiredResourceSets(List<RequiredResourceSet> requiredResourceSets) {
        this.requiredResourceSets = new ArrayList<>(requiredResourceSets);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EffectsContainer getEffects() {
        return effects;
    }

    public void setEffects(EffectsContainer effects) {
        this.effects = effects;
    }
}