package model.card.effects.interfaces;

/**
 * This is the base interface for effects that can be activated once per round
 */
public interface OncePerRoundEffectInterface extends EffectInterface {
    /**
     * Checks if the effect has not been activated during the current turn
     *
     * @param currentTurn
     * @return
     */
    boolean isAlreadyActivated(int currentTurn);

    /**
     * Marks the effect activated during the current turn
     *
     * @param currentTurn
     */
    void markActivated(int currentTurn);
}
