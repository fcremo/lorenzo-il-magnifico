package model.card.effects;

import model.card.effects.interfaces.EffectInterface;

public class SkipFirstTurnEffect implements EffectInterface {
    @Override
    public String toString() {
        return "each round, you skip your first turn. You start taking actions from the second turn. When all players have taken all their turns, you may still place your last Family Member";
    }
}
