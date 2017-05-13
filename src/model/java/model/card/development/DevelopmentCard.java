package model.card.development;

import model.action.Action;
import model.card.Card;
import model.resource.RequiredResourceSet;

import java.util.List;

/**
 * The base class representing development cards
 */
public abstract class DevelopmentCard extends Card {
    private int period;

    /**
     * An action that the player can perform when taking the card.
     */
    private Action immediateAction;

    public DevelopmentCard(String id, String name, List<RequiredResourceSet> requiredResourceSet, int period, Action immediateAction) {
        super(id, name, requiredResourceSet);
        this.period = period;
        this.immediateAction = immediateAction;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Action getImmediateAction() {
        return immediateAction;
    }

    public void setImmediateAction(Action immediateAction) {
        this.immediateAction = immediateAction;
    }
}
