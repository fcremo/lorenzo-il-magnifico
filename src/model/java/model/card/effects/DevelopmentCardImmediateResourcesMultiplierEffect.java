package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResource;
import model.resource.ObtainedResourceSet;

import java.util.ArrayList;

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

    public ArrayList<ObtainedResourceSet> multiplyObtainedResourceSet(ArrayList<ObtainedResourceSet> currentObtainedResourceSets){
        return null;
        // TODO
    }

    @Override
    public String toString() {
        return "Each time you receive " + resourceType + " as an immediate effect from Development Cards, you receive the resources " + multiplier + " times.";
    }
}
