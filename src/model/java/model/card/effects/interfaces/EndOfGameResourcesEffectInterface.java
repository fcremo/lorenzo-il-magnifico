package model.card.effects.interfaces;

import model.resource.ObtainedResourceSet;

/**
 * This is the interface for the effects that give bonus resources at the end of the game
 */
public interface EndOfGameResourcesEffectInterface {
    ObtainedResourceSet getResources();
}
