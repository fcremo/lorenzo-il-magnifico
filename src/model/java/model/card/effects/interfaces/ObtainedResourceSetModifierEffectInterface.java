package model.card.effects.interfaces;

import model.resource.ObtainedResourceSet;

import java.util.List;

/**
 * This is the interface for a generic effect that modifies the resources obtained by the player by any mean,
 * including immediate effects, productions, harvests, action space bonuses, etc
 */
public interface ObtainedResourceSetModifierEffectInterface extends EffectInterface {
    /**
     * Compute the list of resource sets that the player can choose from
     *
     * @param currentResources the current list of resource sets that the player can receive
     * @return the modified list of resource sets the player can choose from
     */
    List<ObtainedResourceSet> modifyResources(List<ObtainedResourceSet> currentResources);
}
