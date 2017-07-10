package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;

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
     * @param currentObtainableResourceSets the current obtained resource set
     * @return the updated obtained resource set
     */
    public List<ObtainableResourceSet> multiplyObtainedResourceSet(List<ObtainableResourceSet> currentObtainableResourceSets) {
        List<ObtainableResourceSet> newResourceSets = new ArrayList<>();

        for (ObtainableResourceSet obtainableResourceSet : currentObtainableResourceSets) {
            ObtainableResourceSet newObtainableResourceSet = new ObtainableResourceSet(obtainableResourceSet);
            int newValue = obtainableResourceSet.getObtainedAmount(resourceType) * multiplier;
            newObtainableResourceSet.setObtainedAmount(resourceType, newValue);
            newResourceSets.add(obtainableResourceSet);
        }

        return newResourceSets;
    }

    @Override
    public String toString() {
        return "each time you receive " + resourceType + " as an immediate effect from Development Cards, you receive the resources " + multiplier + " times";
    }
}
