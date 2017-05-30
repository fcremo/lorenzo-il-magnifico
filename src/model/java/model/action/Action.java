package model.action;

public class Action {
    private ActionType actionType;
    private int actionValue;

    public Action(ActionType actionType, int actionValue) {
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
}
