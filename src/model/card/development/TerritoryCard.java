package model.card.development;

import model.action.ActionType;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This class represents a territory card
 */
public class TerritoryCard extends DevelopmentCard {
    private ObtainableResourceSet harvestResourceSet;
    private int requiredValueForHarvest;

    private TerritoryCard() {
        super();
    }

    public TerritoryCard(String name, int period, ObtainableResourceSet harvestResourceSet, int requiredValueForHarvest) {
        super(name, new ArrayList<>(), period);
        this.harvestResourceSet = harvestResourceSet;
        this.requiredValueForHarvest = requiredValueForHarvest;
    }

    public ObtainableResourceSet getHarvestResourceSet() {
        return harvestResourceSet;
    }

    public void setHarvestResourceSet(ObtainableResourceSet harvestResourceSet) {
        this.harvestResourceSet = harvestResourceSet;
    }

    public int getRequiredValueForHarvest() {
        return requiredValueForHarvest;
    }

    public void setRequiredValueForHarvest(int requiredValueForHarvest) {
        this.requiredValueForHarvest = requiredValueForHarvest;
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_TERRITORY_CARD;
    }

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
        }

        if (string.lastIndexOf("or ") != -1) {
            string.delete(string.lastIndexOf("or "), string.lastIndexOf("or ") + 3);
        }

        if (getEffectsContainer() != null && !getEffectsContainer().getEffects().isEmpty()) {
            string.append("effects: ")
                  .append(getEffectsContainer().toString());
        }

        if (!getHarvestResourceSet().isEmpty()) {
            string.append(String.format("Harvest (at %d): ", getRequiredValueForHarvest()))
                  .append(getHarvestResourceSet().toString())
                  .append("\n");
        }

        return string.toString();
    }

    public static class InstanceCreator implements com.google.gson.InstanceCreator<TerritoryCard> {
        @Override
        public TerritoryCard createInstance(Type type) {
            return new TerritoryCard();
        }
    }
}
