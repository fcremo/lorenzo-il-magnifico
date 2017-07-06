package model.card.effects;

import model.card.development.DevelopmentCard;
import model.card.effects.interfaces.EffectInterface;

/**
 * This effect blocks the player from receiving victory points from a certain development card type
 * at the end of the game
 */
public class IgnoreEndOfGameVictoryPointsFromDevelopmentCardsEffect implements EffectInterface {
    private Class<? extends DevelopmentCard> developmentCardType;

    public IgnoreEndOfGameVictoryPointsFromDevelopmentCardsEffect(Class<? extends DevelopmentCard> developmentCardType) {
        this.developmentCardType = developmentCardType;
    }

    /**
     * @param developmentCardType
     * @return true if the victory points from the provided type of development cards must be ignored
     */
    public boolean ignoreVictoryPointsFromDevelopmentCard(Class<? extends DevelopmentCard> developmentCardType) {
        return developmentCardType == this.developmentCardType;
    }

    @Override
    public String toString() {
        return "At the end of the game, you don’t score points for any of your " + developmentCardType;
    }
}

