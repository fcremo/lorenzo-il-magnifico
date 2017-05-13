package model.card.effects.interfaces;

import model.board.actionspace.ActionSpace;
import model.resource.RequiredResourceSet;

/**
 * This is the interface for an effects that sets the cost required to occupy a floor in an already occupied tower
 */
public interface ActionSpaceDoubleOccupationCostSetterEffectInterface extends EffectInterface {
    /**
     * Compute the set of resources that a player has to pay to occupy a floor in an already occupied tower
     *
     * @param currentResources the current set of resources to be paid
     * @param actionSpace      the action space to be occupied
     * @return the modified set of resources needed to occupy the action space
     */
    RequiredResourceSet setResources(RequiredResourceSet currentResources, ActionSpace actionSpace);
}
