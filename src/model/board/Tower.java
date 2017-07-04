package model.board;

import model.board.actionspace.Floor;
import model.card.development.DevelopmentCard;
import model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the state of one tower
 */
public class Tower<T extends DevelopmentCard> implements Serializable {
    private ArrayList<Floor<T>> floors = new ArrayList<>();

    /**
     * Returns the card on the specified floor
     *
     * @param floor the floor
     * @returns the card
     */
    public T getCard(int floor) {
        return floors.get(floor).getCard();
    }

    public void setCards(List<T> developmentCards) {
        for(int i=0; i<developmentCards.size(); i++){
            floors.get(i).setCard(developmentCards.get(i));
        }
    }

    /**
     * @param player the player you want to check
     * @return true if the player is occupying the tower with any of his family members
     */
    public boolean isOccupiedBy(Player player) {
        return floors.stream().anyMatch(
                floor -> floor.getOccupants().stream().map(occupation -> occupation.first).equals(player)
        );
    }

    /**
     * @return true if no players are occupying any floor
     */
    public boolean isOccupied() {
        return floors.stream().noneMatch(
                floor -> floor.getOccupants().isEmpty()
        );
    }

    public void setFloors(ArrayList<Floor<T>> floors) {
        this.floors = floors;
    }

    public ArrayList<Floor<T>> getFloors() {
        return floors;
    }
}
