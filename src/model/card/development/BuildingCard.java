package model.card.development;

import model.action.ActionType;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class BuildingCard extends DevelopmentCard {
    private ArrayList<Tuple<RequiredResourceSet, ObtainableResourceSet>> productions;
    private int requiredValueForProduction;

    public BuildingCard(String id, String name, RequiredResourceSet requiredResourceSet, int period,
                        List<Tuple<RequiredResourceSet, ObtainableResourceSet>> productions,
                        int requiredValueForProduction) {
        super(id, name, new ArrayList<>(), period);

        // Set required resource sets
        ArrayList<RequiredResourceSet> requiredResourceSets = new ArrayList<>();
        requiredResourceSets.add(requiredResourceSet);
        this.setRequiredResourceSet(requiredResourceSets);

        this.productions = new ArrayList<>(productions);
        this.requiredValueForProduction = requiredValueForProduction;
    }

    public List<Tuple<RequiredResourceSet, ObtainableResourceSet>> getProductions() {
        return productions;
    }

    public void setProductions(List<Tuple<RequiredResourceSet, ObtainableResourceSet>> productions) {
        this.productions = new ArrayList<>(productions);
    }

    public int getRequiredValueForProduction() {
        return requiredValueForProduction;
    }

    public void setRequiredValueForProduction(int requiredValueForProduction) {
        this.requiredValueForProduction = requiredValueForProduction;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(getName());
        string.append("\n");

        if (getRequiredResourceSet() != null && !getRequiredResourceSet().isEmpty()) {
            string.append("price: ");
            for (RequiredResourceSet requirement : getRequiredResourceSet()) {
                string.append(requirement)
                      .append("\n")
                      .append("or ");
            }
        }

        if (string.lastIndexOf("or ") != -1) {
            string.delete(string.lastIndexOf("or "), string.lastIndexOf("or ") + 3);
        }

        if (getEffectsContainer() != null && !getEffectsContainer().getEffects().isEmpty()) {
            string.append("effects: ")
                  .append(getEffectsContainer().toString());
        }

        if (!getProductions().isEmpty()) {
            string.append(String.format("Production (at %d): ", getRequiredValueForProduction()));

            for(Tuple<RequiredResourceSet, ObtainableResourceSet> production : getProductions()) {
                RequiredResourceSet required = production.first;
                ObtainableResourceSet obtained = production.second;

                string.append(obtained);
                if (required != null && !required.isEmpty()) {
                    string.append(" for ")
                          .append(required);
                }
                string.append("\n")
                      .append("or ");
            }

            if (string.lastIndexOf("or ") != -1) {
                string.delete(string.lastIndexOf("or "), string.lastIndexOf("or ") + 3);
            }
        }

        return string.toString();
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_BUILDING_CARD;
    }
}
