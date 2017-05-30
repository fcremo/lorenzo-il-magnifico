package model.card.effects.interfaces;

import model.resource.ObtainedResourceSet;

/**
 * This class implements the effect that gives the player
 * a set of resources at the end of the game (usually victory points)
 */
public class EndOfGameResourcesEffect implements EffectInterface {
    ObtainedResourceSet resourceSet;

    public EndOfGameResourcesEffect(ObtainedResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }

    public ObtainedResourceSet getResourceSet() {
        return resourceSet;
    }

    public void setResourceSet(ObtainedResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }
}
