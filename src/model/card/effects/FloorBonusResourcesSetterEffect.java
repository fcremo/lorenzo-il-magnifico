package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResourceSet;

/**
 * This effect sets the resources obtained when occupying any floor
 */
public class FloorBonusResourcesSetterEffect implements EffectInterface {
    private ObtainableResourceSet obtainableResourceSet;

    public FloorBonusResourcesSetterEffect(ObtainableResourceSet obtainableResourceSet) {
        this.obtainableResourceSet = obtainableResourceSet;
    }

    public ObtainableResourceSet setObtainedResourceSet() {
        return obtainableResourceSet;
    }

    @Override
    public String toString() {
        if (obtainableResourceSet.isEmpty()) {
            return "You don’t take the bonuses when you take a Development Card from the third and the fourth floor of the towers.";
        }
        else {
            return "You take " + obtainableResourceSet + " when you take a Development Card from the third and the fourth floor of the towers.";
        }
    }
}
