package model.card;

import com.google.gson.annotations.SerializedName;
import model.card.effects.EffectsContainer;
import model.resource.RequiredResourceSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The base class representing a leader or development card
 */
public class Card implements Serializable {
    /**
     * The unique id of the card
     */
    private UUID id;

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
    @SerializedName("effects")
    private EffectsContainer effectsContainer;

    /**
     * No-parameters constructor needed for creating an instance with a UUID
     * when deserializing from JSON.
     * See https://github.com/google/gson/issues/513
     */
    protected Card() {
        this.id = UUID.randomUUID();
        this.requiredResourceSet = new ArrayList<>();
        this.effectsContainer = new EffectsContainer();
    }

    public Card(String name, List<RequiredResourceSet> requiredResourceSet) {
        this();
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

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EffectsContainer getEffectsContainer() {
        return effectsContainer;
    }

    public void setEffectsContainer(EffectsContainer effectsContainer) {
        this.effectsContainer = effectsContainer;
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
        StringBuilder string = new StringBuilder(getName());
        string.append("\n");

        if (getRequiredResourceSet() != null && !getRequiredResourceSet().isEmpty()) {
            string.append("price: ");
            for (RequiredResourceSet requirement : getRequiredResourceSet()) {
                string.append(requirement)
                      .append("\n")
                      .append("or ");
            }
            if (string.lastIndexOf("or ") != -1) {
                string.delete(string.lastIndexOf("or "), string.lastIndexOf("or ") + 3);
            }
        }

        if (getEffectsContainer() != null && !getEffectsContainer().getEffects().isEmpty()) {
            string.append("effects: ")
                  .append(getEffectsContainer().toString())
                  .append("\n");
        }

        return string.toString();
    }


    /**
     * @param resources
     * @return true if "resources" is one of the required resource sets of the card,
     *          or if "resources" is empty and the card has no cost
     */
    public boolean isPayableWith(RequiredResourceSet resources) {
        if(requiredResourceSet.size() == 0 && resources.isEmpty()) return true;

        else return requiredResourceSet.contains(resources);
    }
}