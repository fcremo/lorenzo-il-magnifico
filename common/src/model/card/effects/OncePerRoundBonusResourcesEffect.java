package model.card.effects;

import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.resource.ObtainableResourceSet;

/**
 * This effect gives the player some bonus resources once per round
 */
public class OncePerRoundBonusResourcesEffect implements OncePerRoundEffectInterface {
    private int lastActivatedAt = 0;

    private ObtainableResourceSet resources;

    public OncePerRoundBonusResourcesEffect(ObtainableResourceSet resources) {
        this.resources = resources;
    }

    /**
     * @return the set of resources obtained
     */
    public ObtainableResourceSet getResources() {
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

    @Override
    public String toString() {
        return "Once per round receive " + resources;
    }
}
