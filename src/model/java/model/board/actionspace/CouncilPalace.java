package model.board.actionspace;

import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainedResourceSet;

public class CouncilPalace extends ActionSpace {
    public CouncilPalace(ObtainedResourceSet bonus, int requiredFamilyMemberValue) {
        super(bonus, requiredFamilyMemberValue);
    }

    @Override
    public boolean canGoThere(Player player, FamilyMemberColor familyMember) {
        // TODO: 5/24/17
        return false;
    }
}
