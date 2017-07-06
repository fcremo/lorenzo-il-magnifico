package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResource;
import model.resource.ObtainedResourceSet;

import java.util.ArrayList;
import java.util.List;

/**
 * This effect multiplies the resources obtained from an immediate effect of a development card
 */
public class DevelopmentCardImmediateResourcesMultiplierEffect implements EffectInterface {
    private int multiplier;
    private ObtainableResource resourceType;

    public DevelopmentCardImmediateResourcesMultiplierEffect(int multiplier, ObtainableResource resourceType) {
        this.multiplier = multiplier;
        this.resourceType = resourceType;
    }

    /**
     * Modifies the resource set obtained from the card according to multipliers
     * @param currentObtainedResourceSets the current obtained resource set
     * @return the updated obtained resource set
     */
    public List<ObtainedResourceSet> multiplyObtainedResourceSet(List<ObtainedResourceSet> currentObtainedResourceSets) {
        List<ObtainedResourceSet> newResourceSets = new ArrayList<>();

        for (ObtainedResourceSet obtainedResourceSet : currentObtainedResourceSets) {
            ObtainedResourceSet newObtainedResourceSet = new ObtainedResourceSet(obtainedResourceSet);
            int newValue = obtainedResourceSet.getObtainedAmount(resourceType) * multiplier;
            newObtainedResourceSet.setObtainedAmount(resourceType, newValue);
            newResourceSets.add(obtainedResourceSet);
        }

        return newResourceSets;
    }

    @Override
    public String toString() {
        return "Each time you receive " + resourceType + " as an immediate effect from Development Cards, you receive the resources " + multiplier + " times";
    }
}
