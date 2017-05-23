package model.card.effects.interfaces;

import model.action.Action;

public interface OncePerTurnActionEffectInterface extends OncePerTurnEffectInterface {
    Action getAction();
}
