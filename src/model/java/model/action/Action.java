package model.action;

import model.player.Player;
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;

import java.util.List;

public abstract class Action {
    /**
     * Checks whether the player can perform the action
     *
     * @param player the player who wants to perform the action
     * @return whether the player can perform the action
     */
    public abstract boolean canPerformAction(Player player);

    /**
     * Returns the resources that the player can choose to spend to perform the action.
     * For example the action "take a card" may cost some gold OR (some wood and some stone).
     * Only one of these ResourceSet must be spent to perform the action.
     *
     * @return a list of alternative RequiredResourceSet needed to perform the action
     */
    public abstract List<RequiredResourceSet> getRequiredResourceSets();

    /**
     * Returns the resources that the player can obtain when he performs the action.
     * He can choose only one of the ResourceSet in the list.
     *
     * @return a list of alternative ObtainedResourceSet obtainable from the action
     */
    public abstract List<ObtainedResourceSet> getObtainableResourceSets();
}
