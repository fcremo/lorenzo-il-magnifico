package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainedResourceSet;

/**
 * This effect gives the player some resources when taking a card
 */
public class ImmediateResourcesEffect implements EffectInterface {
    private ObtainedResourceSet obtainedResourceSet;

    public ImmediateResourcesEffect(ObtainedResourceSet obtainedResourceSet) {
        this.obtainedResourceSet = obtainedResourceSet;
    }

    /**
     * @return the set of resources obtained
     */
    public ObtainedResourceSet getObtainedResourceSet() {
        return obtainedResourceSet;
    }
}
