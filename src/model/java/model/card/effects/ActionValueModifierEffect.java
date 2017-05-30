package model.card.effects;

import model.action.Action;
import model.action.ActionType;
import model.card.effects.interfaces.ActionValueModifierEffectInterface;

public class ActionValueModifierEffect implements ActionValueModifierEffectInterface {
    private ActionType actionType;
    private int value;

    public ActionValueModifierEffect(ActionType actionType, int value) {
        this.actionType = actionType;
        this.value = value;
    }

    @Override
    public int modifyValue(int currentValue, Action action) {
        if(action.getActionType() == actionType){
            return action.getActionValue() + value;
        }
        else {
            return currentValue;
        }
    }
}
