package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class SmallProductionArea extends ActionSpace {
    public SmallProductionArea(ObtainableResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public String getShortDescriptionForChoosing() {
        return "Small production area";
    }
}
