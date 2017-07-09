package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class BigHarvestArea extends ActionSpace {
    public BigHarvestArea(ObtainableResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public String getShortDescriptionForChoosing() {
        return "Big harvest area";
    }
}
