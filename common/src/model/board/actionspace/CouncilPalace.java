package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class CouncilPalace extends ActionSpace {
    public CouncilPalace(ObtainableResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public String getShortDescriptionForChoosing() {
        return "Council palace";
    }
}
