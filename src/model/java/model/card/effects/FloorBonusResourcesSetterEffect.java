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

}
