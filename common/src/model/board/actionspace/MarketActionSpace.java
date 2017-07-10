package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class MarketActionSpace extends ActionSpace {
    public MarketActionSpace(ObtainableResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public String toString() {
        String string;

        if (isOccupied()) {
            string = getOccupantsString();
        }
        else {
            string = getBonus().toString();
        }

        return string;
    }

    @Override
    public String getShortDescriptionForChoosing() {
        return "Market: " + getBonus().toString();
    }
}

