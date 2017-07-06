package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class SmallProductionArea extends ActionSpace {
    public SmallProductionArea(ObtainableResourceSet bonus, int requiredFamilyMemberValue, String id) {
        super(bonus, requiredFamilyMemberValue, id);
    }
}
