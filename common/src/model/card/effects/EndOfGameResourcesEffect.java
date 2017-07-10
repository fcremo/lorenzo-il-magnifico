package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResourceSet;

/**
 * This class implements the effect that gives the player
 * a set of resources at the end of the game.
 * N.B.: The resources can be negative
 */
public class EndOfGameResourcesEffect implements EffectInterface {
    private ObtainableResourceSet resourceSet;

    public EndOfGameResourcesEffect(ObtainableResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }

    public ObtainableResourceSet getResourceSet() {
        return resourceSet;
    }

    @Override
    public String toString() {
        return "at the end of the game you get " + resourceSet;
    }
}
