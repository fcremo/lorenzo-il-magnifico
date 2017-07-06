package model.card.development;

import model.action.ActionType;
import model.resource.ObtainableResourceSet;

import java.util.ArrayList;

/**
 * This class represents a territory card
 */
public class TerritoryCard extends DevelopmentCard {
    private ObtainableResourceSet harvestResourceSet;
    private int requiredValueForHarvest;

    public TerritoryCard(String id, String name, int period, ObtainableResourceSet harvestResourceSet, int requiredValueForHarvest) {
        super(id, name, new ArrayList<>(), period);
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
}
