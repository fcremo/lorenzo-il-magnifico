package model.card.effects.interfaces;

/**
 * This is the interface for effects that allow the player to ignore
 * the constraint on military points needed to take territory cards
 */
public interface SkipMilitaryPointsRequirementCheckEffectInterface extends EffectInterface {
    /**
     * Decide whether the player can ignore the military points constraint when taking territory cards
     *
     * @return true if the player can take territory cards regardless of his military points
     */
    boolean skipMilitaryPointsRequirement();

}
