package model.card.effects.interfaces;

import model.board.actionspace.ActionSpace;

/**
 * This is the interface for the effects that inhibit the player from occupying one or more action spaces
 */
public interface InhibitActionSpaceEffectInterface extends EffectInterface {
    /**
     * Decide whether occupying the action space is prohibited
     *
     * @param actionSpace the action space to be occupied
     * @return true if the player cannot occupy the action space.
     * <b>If true no further checks have to be performed, the player can't go there</b>
     */
    boolean isInhibited(ActionSpace actionSpace);
}
