package model.card.effects.interfaces;

import model.resource.ObtainedResourceSet;

/**
 * This is the interface for the effects that give the player a bonus each time he/she
 * decides to give his/her support to the church (e.g. decides to not to be excommunicated)
 */
public interface ChurchSupportBonusResourcesEffectInterface extends EffectInterface {
    /**
     * Compute the set of bonus resources the player receives if he/she decides not to be excommunicated
     *
     * @return the set of resources
     */
    ObtainedResourceSet getResources();
}
