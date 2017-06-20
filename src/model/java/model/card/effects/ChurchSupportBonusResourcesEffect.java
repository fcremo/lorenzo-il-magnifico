package model.card.effects;

import model.card.effects.interfaces.EffectInterface;
import model.resource.ObtainedResourceSet;

/**
 * This effect gives the player a bonus resource set when he shows support for the Church
 */
public class ChurchSupportBonusResourcesEffect implements EffectInterface {
    private ObtainedResourceSet bonusResources;

    public ChurchSupportBonusResourcesEffect(ObtainedResourceSet bonusResources) {
        this.bonusResources = bonusResources;
    }

    public ObtainedResourceSet getBonusResources() {
        return bonusResources;
    }
}
