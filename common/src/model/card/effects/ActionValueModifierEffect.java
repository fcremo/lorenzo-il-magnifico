package model.card.effects;

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
     * @param actionType   the type of action being performed
     * @return
     */
    public int modifyValue(int currentValue, ActionType actionType) {
        if (this.actionType == actionType) {
            return currentValue + value;
        }
        else {
            return currentValue;
        }
    }

    @Override
    public String toString() {
        if (value > 0) {
            return "increase your action value by " + value + " when you perform " + actionType;
        }
        else {
            return "decrease your action value by " + Math.abs(value) + " when you perform " + actionType;
        }
    }
}
