package model.board.actionspace;

import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainedResourceSet;

public class MarketActionSpace extends ActionSpace {
    public MarketActionSpace(ObtainedResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public boolean canGoThere(Player player, FamilyMemberColor familyMember) {
        // TODO: 5/25/17  
        return false;
    }
}

