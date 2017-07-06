package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class SmallHarvestArea extends ActionSpace {
    public SmallHarvestArea(ObtainableResourceSet bonus, int requiredFamilyMemberValue, String id) {
        super(bonus, requiredFamilyMemberValue, id);
    }
}
