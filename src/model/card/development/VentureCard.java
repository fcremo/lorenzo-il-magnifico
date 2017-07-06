package model.card.development;

import model.action.ActionType;
import model.resource.RequiredResourceSet;

import java.util.List;

public class VentureCard extends DevelopmentCard {
    public VentureCard(String id, String name, List<RequiredResourceSet> requiredResourceSet, int period) {
        super(id, name, requiredResourceSet, period);
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_VENTURE_CARD;
    }
}
