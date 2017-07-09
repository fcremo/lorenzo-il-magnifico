package model.card.development;

import model.action.ActionType;
import model.resource.RequiredResourceSet;

import java.lang.reflect.Type;
import java.util.List;

public class VentureCard extends DevelopmentCard {
    private VentureCard() {
        super();
    }

    public VentureCard(String name, List<RequiredResourceSet> requiredResourceSet, int period) {
        super(name, requiredResourceSet, period);
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_VENTURE_CARD;
    }

    public static class InstanceCreator implements com.google.gson.InstanceCreator<VentureCard> {
        @Override
        public VentureCard createInstance(Type type) {
            return new VentureCard();
        }
    }
}
