package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class BigProductionArea extends ActionSpace {
    public BigProductionArea(ObtainableResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public String getShortDescriptionForChoosing() {
        return "Big production area";
    }
}
