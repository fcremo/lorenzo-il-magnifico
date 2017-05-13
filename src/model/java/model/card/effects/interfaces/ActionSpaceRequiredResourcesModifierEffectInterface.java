package model.card.effects.interfaces;

import model.board.actionspace.ActionSpace;
import model.resource.RequiredResourceSet;

/**
 * This is the interface for the effects that modify the cost of occupying an action space
 */
public interface ActionSpaceRequiredResourcesModifierEffectInterface extends EffectInterface {
    /**
     * Compute the set of resources needed to occupy an action space
     *
     * @param currentRequiredResourceSet the current set of resources
     * @param actionSpace                the action space to be occupied
     * @return the new set of resources
     */
    RequiredResourceSet modifyResources(RequiredResourceSet currentRequiredResourceSet, ActionSpace actionSpace);

}
