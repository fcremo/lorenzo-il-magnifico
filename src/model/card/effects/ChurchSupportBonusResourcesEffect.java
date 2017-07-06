package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainableResourceSet;

/**
 * This effect gives the player a bonus resource set when he shows support for the Church
 */
public class ChurchSupportBonusResourcesEffect implements EffectInterface {
    private ObtainableResourceSet bonusResources;

    public ChurchSupportBonusResourcesEffect(ObtainableResourceSet bonusResources) {
        this.bonusResources = bonusResources;
    }

    public ObtainableResourceSet getBonusResources() {
        return bonusResources;
    }

    @Override
    public String toString() {
        return "You gain " + bonusResources + " when you support the Church in a Vatican Report phase.";
    }
}
