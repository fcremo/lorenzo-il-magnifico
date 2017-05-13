package model.card.effects.interfaces;

/**
 * This is the base interface for effects that can be activated once per turn
 */
public interface OncePerTurnEffectInterface extends EffectInterface {
    boolean isAlreadyActivated(int currentTurn);
}
