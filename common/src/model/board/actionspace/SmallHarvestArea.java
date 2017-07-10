package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class SmallHarvestArea extends ActionSpace {
    public SmallHarvestArea(ObtainableResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public String getShortDescriptionForChoosing() {
        return "Small harvest area";
    }
}
