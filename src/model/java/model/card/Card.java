package model.card;

import model.card.effects.EffectsContainer;
import model.resource.RequiredResourceSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The base class representing a leader or development card
 */
public class Card implements Serializable {
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
    private ArrayList<RequiredResourceSet> requiredResourceSet;

    /**
     * The effects of the card
     */
    private EffectsContainer effects;

    public Card(String id, String name, List<RequiredResourceSet> requiredResourceSet) {
        this.id = id;
        this.name = name;
        this.requiredResourceSet = new ArrayList<>(requiredResourceSet);
    }

    /**
     * Returns a list of resources that the player can pay to get the card.
     * Only one of them is required (e.g. 2 gold OR (1 wood and 1 stone))
     *
     * @return the list of resources
     */
    public List<RequiredResourceSet> getRequiredResourceSet() {
        return requiredResourceSet;
    }

    public void setRequiredResourceSet(List<RequiredResourceSet> requiredResourceSet) {
        this.requiredResourceSet = new ArrayList<>(requiredResourceSet);
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

    @Override
    public boolean equals(Object card) {
        if (this == card) return true;
        if (!(card instanceof Card)) return false;

        Card otherCard = (Card) card;

        return id.equals(otherCard.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name  +
                "\n requirements: " + requiredResourceSet +
                "\n effects: " + effects;
    }
}