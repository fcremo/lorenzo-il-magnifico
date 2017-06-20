package model.card.development;

import model.card.Card;
import model.resource.RequiredResourceSet;

import java.util.List;

/**
 * The base class representing development cards
 */
public abstract class DevelopmentCard extends Card {
    private int period;

    public DevelopmentCard(String id, String name, List<RequiredResourceSet> requiredResourceSet, int period) {
        super(id, name, requiredResourceSet);
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
