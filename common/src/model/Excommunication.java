package model;

import model.card.effects.EffectsContainer;

import java.io.Serializable;

/**
 * This class represents an excommunication
 */
public class Excommunication implements Serializable {
    private int period;

    private EffectsContainer effectsContainer;

    public Excommunication(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public EffectsContainer getEffectsContainer() {
        return effectsContainer;
    }

    public void setEffectsContainer(EffectsContainer effectsContainer) {
        this.effectsContainer = effectsContainer;
    }
}
