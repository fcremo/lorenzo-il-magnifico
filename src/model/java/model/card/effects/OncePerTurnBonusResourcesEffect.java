package model.card.effects;

import model.card.effects.interfaces.OncePerTurnEffectInterface;
import model.resource.ObtainedResourceSet;

/**
 * This effect gives the player some bonus resources once per turn
 */
public class OncePerTurnBonusResourcesEffect implements OncePerTurnEffectInterface {
    private int lastActivatedAt = 0;

    private ObtainedResourceSet resources;

    public OncePerTurnBonusResourcesEffect(ObtainedResourceSet resources) {
        this.resources = resources;
    }

    /**
     * @return the set of resources obtained
     */
    public ObtainedResourceSet getResources() {
        return resources;
    }

    @Override
    public boolean isAlreadyActivated(int currentTurn) {
        return currentTurn <= lastActivatedAt;
    }

    @Override
    public void markActivated(int currentTurn) {
        lastActivatedAt = currentTurn;
    }
}
