package model.card.development;

import model.action.ActionType;
import model.resource.RequiredResourceSet;

import java.util.ArrayList;

public class CharacterCard extends DevelopmentCard {
    public CharacterCard(String id, String name, RequiredResourceSet requiredResourceSet, int period) {
        super(id, name, new ArrayList<>(), period);

        ArrayList<RequiredResourceSet> requiredResourceSets = new ArrayList<>();
        requiredResourceSets.add(requiredResourceSet);
        this.setRequiredResourceSet(requiredResourceSets);
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_CHARACTER_CARD;
    }
}
