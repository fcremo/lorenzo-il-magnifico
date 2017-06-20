package model.card.development;

import model.resource.ObtainedResourceSet;

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
}
