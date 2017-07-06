package model.card.development;

import model.action.ActionType;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
        StringBuilder returnString = new StringBuilder();
        returnString.append(getName() + "\n");

        if(!getRequiredResourceSet().isEmpty()) {
            returnString.append("Requires: ");
            StringJoiner sj = new StringJoiner(" or ");
            for(RequiredResourceSet requiredResourceSet : getRequiredResourceSet()) {
                sj.add(requiredResourceSet.toString());
            }
            returnString.append(sj)
                        .append("\n");
        }

        returnString.append("Effects: ")
                    .append(getEffectsContainer())
                    .append("\n");

        if(!getProductions().isEmpty()) {
            returnString.append(String.format("Production (at %d):", getRequiredValueForProduction()));
            StringJoiner sj = new StringJoiner(" or ");

            for(Tuple<RequiredResourceSet, ObtainableResourceSet> production : getProductions()) {
                RequiredResourceSet requirement = production.first;
                ObtainableResourceSet obtained = production.second;

                sj.add(requirement + " => " + obtained);
            }

            returnString.append(sj)
                        .append("\n");
        }


        return returnString.toString();
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_BUILDING_CARD;
    }
}