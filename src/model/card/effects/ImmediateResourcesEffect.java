package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResourceSet;

/**
 * This effect gives the player some resources when taking a card
 */
public class ImmediateResourcesEffect implements EffectInterface {
    private ObtainableResourceSet obtainableResourceSet;

    public ImmediateResourcesEffect(ObtainableResourceSet obtainableResourceSet) {
        this.obtainableResourceSet = obtainableResourceSet;
    }

    /**
     * @return the set of resources obtained
     */
    public ObtainableResourceSet getObtainableResourceSet() {
        return obtainableResourceSet;
    }

    @Override
    public String toString() {
        return "Receive " + obtainableResourceSet + ".";
    }
}
