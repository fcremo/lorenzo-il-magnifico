package model.board.actionspace;

import model.board.Tower;
import model.card.development.DevelopmentCard;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;

/**
 * This class represents the tower floors, keeping track of whether it's occupied and the relative card and optional bonus.
 */
public class Floor<T extends DevelopmentCard> extends ActionSpace {
    private Tower<T> tower;

    private T card;

    public Floor(ObtainableResourceSet bonus, int requiredFamilyMemberValue, Tower<T> tower, T card, String id) {
        super(bonus, requiredFamilyMemberValue, id);
        this.tower = tower;
        this.card = card;
    }

    /**
     * Returns the resources needed to occupy the floor.
     * The player has to pay 3 gold if the tower is occupied.
     *
     * @return the resources needed to occupy the action space
     */
    public RequiredResourceSet getRequiredResourceSet() {
        RequiredResourceSet requiredResourceSet = new RequiredResourceSet();
        if(tower.isOccupied()) requiredResourceSet.setRequiredAmount(ObtainableResource.GOLD, 3);
        return requiredResourceSet;
    }

    /**
     * @return the card in the floor
     */
    public T getCard() {
        return card;
    }

    public void setCard(T card) {
        this.card = card;
    }

    public Tower<T> getTower() {
        return tower;
    }

    public void setTower(Tower<T> tower) {
        this.tower = tower;
    }

    @Override
    public String toString() {
        String string;

        if (isOccupied()) {
            string = "Occupied by " + getOccupantsString();
        }
        else {
            if (card == null) {
                string = "No card available";
            }
            else  {
                string = getCard().toString();
            }
        }

        return string;
    }
}
