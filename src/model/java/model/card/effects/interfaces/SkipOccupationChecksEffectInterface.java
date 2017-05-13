package model.card.effects.interfaces;

import model.board.actionspace.ActionSpace;

/**
 * This is the generic interface for the effects that allow the player
 * to occupy some or all the already occupied action spaces
 */
public interface SkipOccupationChecksEffectInterface extends EffectInterface {
    /**
     * Decide wether the player can occupy the action space even if it is already occupied
     *
     * @param actionSpace the action space to be occupied
     * @return true if the player is allowed to occupy the action space regardless.
     * <b>if true the double occupation constraint is ignored and no further checks have to be done</b>
     */
    boolean skipOccupationCheck(ActionSpace actionSpace);
}
