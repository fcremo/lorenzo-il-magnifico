package model.card.development;

import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;

import java.util.ArrayList;

/**
 * This class represents a territory card
 */
public class TerritoryCard extends DevelopmentCard {
    private ObtainedResourceSet harvestResourceSet;
    private int requiredValueForHarvest;

    public TerritoryCard(String id, String name, int period, ObtainedResourceSet harvestResourceSet, int requiredValueForHarvest) {
        super(id, name, new ArrayList<>(), period);
        this.harvestResourceSet = harvestResourceSet;
        this.requiredValueForHarvest = requiredValueForHarvest;
    }

    public ObtainedResourceSet getHarvestResourceSet() {
        return harvestResourceSet;
    }

    public void setHarvestResourceSet(ObtainedResourceSet harvestResourceSet) {
        this.harvestResourceSet = harvestResourceSet;
    }

    public int getRequiredValueForHarvest() {
        return requiredValueForHarvest;
    }

    public void setRequiredValueForHarvest(int requiredValueForHarvest) {
        this.requiredValueForHarvest = requiredValueForHarvest;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(getName());
        string.append("\n");

        if (getRequiredResourceSet() != null && !getRequiredResourceSet().isEmpty()) {
            string.append("price: ");
            for (RequiredResourceSet requirement : getRequiredResourceSet()) {
                string.append(requirement);
                string.append("\n");
                string.append("or ");
            }
        }

        if (string.lastIndexOf("or ") != -1) {
            string.delete(string.lastIndexOf("or "), string.lastIndexOf("or ") + 3);
        }

        if (getEffectsContainer() != null && !getEffectsContainer().getEffects().isEmpty()) {
            string.append("effects: ");
            string.append(getEffectsContainer().toString());
        }

        if (!getHarvestResourceSet().isEmpty()) {
            string.append(String.format("Harvest (at %d): ", getRequiredValueForHarvest()));
            string.append(getHarvestResourceSet().toString());
            string.append("\n");
        }

        return string.toString();
    }
}
