package model.card.effects;

import model.action.Action;
import model.action.ActionType;
import model.card.effects.interfaces.OncePerTurnActionEffectInterface;

public class OncePerTurnActionEffect implements OncePerTurnActionEffectInterface {
    /**
     * The turn when the effect was last activated
     */
    private int lastActivatedAt = 0;

    private ActionType actionType;
    private int actionValue;

    public OncePerTurnActionEffect(ActionType actionType, int actionValue) {
        this.actionType = actionType;
        this.actionValue = actionValue;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public int getActionValue() {
        return actionValue;
    }

    public void setActionValue(int actionValue) {
        this.actionValue = actionValue;
    }

    @Override
    public Action getAction() {
        return new Action(actionType, actionValue);
    }

    @Override
    public boolean isAlreadyActivated(int currentTurn) {
        return currentTurn <= lastActivatedAt;
    }

    @Override
    public void markActivated(int currentTurn) {
        lastActivatedAt = currentTurn;
    }
}
