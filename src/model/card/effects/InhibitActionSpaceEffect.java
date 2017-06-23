package model.card.effects;

import model.board.actionspace.ActionSpace;
import model.card.effects.interfaces.EffectInterface;

/**
 * This effect prohibits the player from occupying an action space type
 */
public class InhibitActionSpaceEffect implements EffectInterface {
    private Class<? extends ActionSpace> actionSpaceType;

    public InhibitActionSpaceEffect(Class<? extends ActionSpace> actionSpaceType) {
        this.actionSpaceType = actionSpaceType;
    }

    /**
     * Decide whether occupying the action space is prohibited
     *
     * @param actionSpace the action space to be occupied
     * @return true if the player cannot occupy the action space.
     * <b>If true no further checks have to be performed, the player can't go there</b>
     */
    public boolean isInhibited(ActionSpace actionSpace) {
        return actionSpaceType.isAssignableFrom(actionSpace.getClass());
    }

    @Override
    public String toString() {
        return "You can’t place your Family Members in the " + actionSpaceType + " action spaces.";
    }
}
