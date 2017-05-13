package model.card.effects.interfaces;

import model.resource.ObtainedResourceSet;

/**
 * This is the interface for the effects that modify the set of resources obtained from production or harvest
 */
public interface ProductionHarvestResourceSetModifierEffectInterface extends EffectInterface {
    /**
     * Compute the modified set of resources obtained from production/harvest
     *
     * @param currentObtainedResourceSet the current obtained resource set
     * @return the modified resource set
     */
    ObtainedResourceSet modifyResources(ObtainedResourceSet currentObtainedResourceSet);
}
