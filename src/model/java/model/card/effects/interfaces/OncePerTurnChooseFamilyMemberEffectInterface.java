package model.card.effects.interfaces;

import model.player.FamilyMemberColor;

import java.util.Set;

/**
 * This is the generic interface for the effects that require the player to choose a family member once per turn.
 * Usually implemented by concrete effects in conjunction with other effects that modify a property of that family member.
 * An example would be the effect of Federico Da Montefeltro, which allows the player to choose, once per turn,
 * a colored family member whose value will be set to 6 for that turn.
 */
public interface OncePerTurnChooseFamilyMemberEffectInterface extends OncePerTurnEffectInterface {
    /**
     * Get a set of family members to choose from
     *
     * @return the set of family member colors
     */
    Set<FamilyMemberColor> getChoices();
}
