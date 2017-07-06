package model.board.actionspace;

import model.resource.ObtainableResourceSet;

public class MarketActionSpace extends ActionSpace {
    public MarketActionSpace(ObtainableResourceSet bonus, int requiredFamilyMemberValue, String id) {
        super(bonus, requiredFamilyMemberValue, id);
    }
}

