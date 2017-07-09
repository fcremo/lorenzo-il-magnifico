package model.board.actionspace;

import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainableResourceSet;
import model.util.Tuple;
import ui.cli.contexts.SingleChoiceContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class represents an action space
 */
public abstract class ActionSpace implements Serializable, SingleChoiceContext.Choosable {
    /**
     * A unique id of the action space
     */
    private UUID id = UUID.randomUUID();

    /**
     * True if the action space is enabled
     */
    private boolean isEnabled = true;

    /**
     * List of the players occupying the action space
     * Note that in some cases more players can occupy the same action space
     */
    private ArrayList<Tuple<Player, FamilyMemberColor>> occupants = new ArrayList<>();

    /**
     * The bonus resources obtained when occupying the action space (e.g. top floors and market bonuses)
     */
    private ObtainableResourceSet bonus;

    /**
     * The minimum family member value required to occupy the action space
     */
    private int requiredFamilyMemberValue;

    public ActionSpace(ObtainableResourceSet bonus, int requiredFamilyMemberValue) {
        this.bonus = bonus;
        this.requiredFamilyMemberValue = requiredFamilyMemberValue;
    }

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

    public String getOccupantsString() {
        StringBuilder occupants = new StringBuilder();

        if (!isOccupied()) {
            occupants.append("Not occupied yet");
        }
        else {
            occupants.append("Occupied by ");
            for (int i = 0; i < getOccupants().size(); i++) {
                occupants.append(getOccupants().get(i).first.getUsername())
                         .append(", ");
            }
            if (occupants.lastIndexOf(", ") != -1) {
                occupants.delete(occupants.lastIndexOf(", "), occupants.lastIndexOf(", ") + 2);
            }
        }

        return occupants.toString();
    }

    public void addOccupant(Player player, FamilyMemberColor color) {
        this.occupants.add(new Tuple<>(player, color));
    }

    public void removeAllOccupants() {
        this.occupants.clear();
    }

    public ObtainableResourceSet getBonus() {
        return bonus;
    }

    public void setBonus(ObtainableResourceSet bonus) {
        this.bonus = bonus;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isOccupied() {
        return !occupants.isEmpty();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionSpace)) return false;

        ActionSpace that = (ActionSpace) o;

        return id.equals(that.id);
    }
}
