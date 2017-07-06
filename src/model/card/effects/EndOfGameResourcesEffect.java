package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainedResourceSet;

/**
 * This class implements the effect that gives the player
 * a set of resources at the end of the game.
 * N.B.: The resources can be negative
 */
public class EndOfGameResourcesEffect implements EffectInterface {
    private ObtainedResourceSet resourceSet;

    public EndOfGameResourcesEffect(ObtainedResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }

    public ObtainedResourceSet getResourceSet() {
        return resourceSet;
    }

    @Override
    public String toString() {
        return "At the end of the game you get " + resourceSet;
    }
}
