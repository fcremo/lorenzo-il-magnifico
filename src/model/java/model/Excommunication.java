package model;

import model.card.effects.EffectsContainer;

import java.io.Serializable;

/**
 * This class represents an excommunication
 */
public class Excommunication implements Serializable {
    private int period;

    private EffectsContainer effects;

    public Excommunication(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public EffectsContainer getEffects() {
        return effects;
    }

    public void setEffects(EffectsContainer effects) {
        this.effects = effects;
    }
}
