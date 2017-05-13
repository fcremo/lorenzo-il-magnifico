package model.card.effects.interfaces;

import model.resource.ObtainedResourceSet;

/**
 * This is the interface for the effects that give the player some resources once per turn
 */
public interface OncePerTurnBonusResourcesEffectInterface extends OncePerTurnEffectInterface {
    /**
     * Return the set of resources the player can get
     *
     * @return the set of resources
     */
    ObtainedResourceSet getResources();
}
