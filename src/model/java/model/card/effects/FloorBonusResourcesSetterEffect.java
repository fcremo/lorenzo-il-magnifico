package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainedResourceSet;

/**
 * This effect sets the resources obtained when occupying any floor
 */
public class FloorBonusResourcesSetterEffect implements EffectInterface {
    private ObtainedResourceSet obtainedResourceSet;

    public FloorBonusResourcesSetterEffect(ObtainedResourceSet obtainedResourceSet) {
        this.obtainedResourceSet = obtainedResourceSet;
    }

    public ObtainedResourceSet setObtainedResourceSet(){
        return obtainedResourceSet;
    }

    @Override
    public String toString() {
        if (obtainedResourceSet.isEmpty()) {
            return "You don’t take the bonuses when you take a Development Card from the third and the fourth floor of the towers.";
        }
        else {
            return "You take " + obtainedResourceSet + " when you take a Development Card from the third and the fourth floor of the towers.";
        }
    }
}
