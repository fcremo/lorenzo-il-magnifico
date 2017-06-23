package model.board.actionspace;

import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainedResourceSet;
import model.util.Tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an action space
 */
public abstract class ActionSpace implements Serializable {
    /**
     * List of the players occupying the action space
     * Note that in some cases more players can occupy the same action space
     */
    private ArrayList<Tuple<Player, FamilyMemberColor>> occupants = new ArrayList<>();

    /**
     * The bonus resources obtained when occupying the action space (e.g. top floors and market bonuses)
     */
    private ObtainedResourceSet bonus;

    /**
     * The minimum family member value required to occupy the action space
     */
    private int requiredFamilyMemberValue;

    public ActionSpace(ObtainedResourceSet bonus, int requiredFamilyMemberValue) {
        this.bonus = bonus;
        this.requiredFamilyMemberValue = requiredFamilyMemberValue;
    }

    /**
     * Returns true if the player can occupy the action space.
     * Accounts for card effects and excommunications.
     * E.G.: Ludovico Ariosto allows a user to occupy some already occupied action spaces, and there's an excommunication
     * that forbids to go to the market action spaces.
     * <p>
     * N.B.:    This method does not check if the user has the resources necessary to perform
     * the associated action! (example: taking a card)
     * If the action space is a tower floor the rules specify that a player cannot occupy it unless
     * he can also pay and take the card.
     *
     * @param player the player that wants to occupy the action space
     * @return true if the action space can be occupied by the user
     */
    public abstract boolean canGoThere(Player player, FamilyMemberColor familyMember);

    /**
     * Returns the family member value required to occupy the action space
     * Does not account for bonuses or maluses
     *
     * @return the required family member value
     */
    public int getRequiredFamilyMemberValue() {
        return requiredFamilyMemberValue;
    }

    /**
     * Sets the family member value required to occupy the action space
     *
     * @param requiredFamilyMemberValue the required family member value
     */
    public void setRequiredFamilyMemberValue(int requiredFamilyMemberValue) {
        this.requiredFamilyMemberValue = requiredFamilyMemberValue;
    }

    public List<Tuple<Player, FamilyMemberColor>> getOccupants() {
        return occupants;
    }

    public void addOccupant(Player player, FamilyMemberColor color) {
        this.occupants.add(new Tuple<>(player, color));
    }

    public void removeAllOccupants() {
        this.occupants.clear();
    }

    public ObtainedResourceSet getBonus() {
        return bonus;
    }

    public void setBonus(ObtainedResourceSet bonus) {
        this.bonus = bonus;
    }
}
