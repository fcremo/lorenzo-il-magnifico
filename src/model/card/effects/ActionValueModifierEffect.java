package model.card.effects;

import model.action.Action;
import model.action.ActionType;
import model.card.effects.interfaces.EffectInterface;

/**
 * This effect modifies the family member value for a certain action type
 */
public class ActionValueModifierEffect implements EffectInterface {
    private ActionType actionType;
    private int value;

    public ActionValueModifierEffect(ActionType actionType, int value) {
        this.actionType = actionType;
        this.value = value;
    }

    /**
     * Modify the value of the family member for a given action
     *
     * @param currentValue the current value of the family member
     * @param action       the action being performed
     * @return
     */
    public int modifyValue(int currentValue, Action action) {
        if (action.getActionType() == actionType) {
            return action.getActionValue() + value;
        }
        else {
            return currentValue;
        }
    }

    @Override
    public String toString() {
        if (value > 0) {
            return "Increase your action value by " + value + " when you perform " + actionType + ".";
        }
        else {
            return "Decrease your action value by " + Math.abs(value) + " when you perform " + actionType + ".";
        }
    }
}
