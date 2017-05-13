package model.card.development;

import model.action.Action;
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;

import java.util.List;

public class TerritoryCard extends DevelopmentCard {
    private ObtainedResourceSet harvestResourceSet;
    private int requiredValueForHarvest;

    public TerritoryCard(String id, String name, List<RequiredResourceSet> requiredResourceSet, int period,
                         Action immediateAction, ObtainedResourceSet harvestResourceSet, int requiredValueForHarvest) {
        super(id, name, requiredResourceSet, period, immediateAction);
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
