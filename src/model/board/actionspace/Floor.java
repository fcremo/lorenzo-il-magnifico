package model.board.actionspace;

import model.board.Tower;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.DevelopmentCard;
import model.card.development.TerritoryCard;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import ui.cli.contexts.SingleChoiceContext;

/**
 * This class represents the tower floors, keeping track of whether it's occupied and the relative card and optional bonus.
 */
public class Floor<T extends DevelopmentCard> extends ActionSpace implements SingleChoiceContext.Choosable {
    private Tower<T> tower;

    private T card;

    public Floor(ObtainableResourceSet bonus, int requiredFamilyMemberValue, Tower<T> tower, T card) {
        super(bonus, requiredFamilyMemberValue);
        this.tower = tower;
        this.card = card;
    }

    /**
     * Returns the resources needed to occupy the floor if the tower is already occupied.
     * Does not include the resources needed to take the card.
     *
     * @return the resources needed to occupy the action space
     */
    public RequiredResourceSet getDoubleOccupationCost() {
        RequiredResourceSet requiredResourceSet = new RequiredResourceSet();
        if (tower.isOccupied()) requiredResourceSet.setRequiredAmount(ObtainableResource.GOLD, 3);
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
        StringBuilder sb = new StringBuilder();

        if (isOccupied()) {
            sb.append("Occupied by ")
              .append(getOccupantsString())
              .append("\n");
        }
        else {
            if (card == null) {
                sb.append("Card was taken\n");
            }
            else {
                sb.append(getCard())
                  .append("\n");

                if(!getBonus().isEmpty()) {
                    sb.append("Floor bonus: ")
                      .append(getBonus())
                      .append("\n");
                }

                sb.append("Required value: ")
                  .append(getRequiredFamilyMemberValue());
            }
        }

        return sb.toString();
    }

    @Override
    public String getShortDescriptionForChoosing() {
        String str;
        if (card instanceof TerritoryCard) {
            str = "Territory ";
        }
        else if (card instanceof BuildingCard) {
            str = "Building ";
        }
        else if (card instanceof CharacterCard) {
            str = "Character ";
        }
        else {
            str = "Venture ";
        }

        str += card.getName();

        return str;
    }

}
