package model.card.development;

import model.action.ActionType;
import model.resource.RequiredResourceSet;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CharacterCard extends DevelopmentCard {
    private CharacterCard() {
        super();
    }

    public CharacterCard(String name, RequiredResourceSet requiredResourceSet, int period) {
        super(name, new ArrayList<>(), period);

        ArrayList<RequiredResourceSet> requiredResourceSets = new ArrayList<>();
        requiredResourceSets.add(requiredResourceSet);
        this.setRequiredResourceSet(requiredResourceSets);
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_CHARACTER_CARD;
    }

    public static class InstanceCreator implements com.google.gson.InstanceCreator<CharacterCard> {
        @Override
        public CharacterCard createInstance(Type type) {
            return new CharacterCard();
        }
    }
}
