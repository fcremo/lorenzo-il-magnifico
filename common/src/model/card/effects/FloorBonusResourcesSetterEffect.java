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
            return "you don’t take the third and fourth floor bonuses when you go there";
        }
        else {
            return "you take " + obtainableResourceSet + " when you go to the third and the fourth floor of the towers " +
                    "instead of what's written on the board";
        }
    }
}
