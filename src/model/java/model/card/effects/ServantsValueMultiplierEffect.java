package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

/**
 * This effect multiplies the added value of the servants
 */
public class ServantsValueMultiplierEffect implements EffectInterface {
    private double multiplier;

    public ServantsValueMultiplierEffect(double multiplier) {
        this.multiplier = multiplier;
    }

    public int multiplyServantValue(int currentValue) {
        return (int)Math.round(((double) currentValue) * multiplier);
    }

    @Override
    public String toString() {
        return "You have to spend " + (int)Math.round(1.0 / multiplier) + " servants to increase your action value by 1";
    }
}
