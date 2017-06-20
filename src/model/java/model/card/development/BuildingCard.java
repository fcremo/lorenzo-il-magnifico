package model.card.development;

import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class BuildingCard extends DevelopmentCard {
    private ArrayList<Tuple<RequiredResourceSet, ObtainedResourceSet>> productions;
    private int requiredValueForProduction;

    public BuildingCard(String id, String name, RequiredResourceSet requiredResourceSet, int period,
                        ArrayList<Tuple<RequiredResourceSet, ObtainedResourceSet>> productions,
                        int requiredValueForProduction) {
        super(id, name, new ArrayList<>(), period);

        // Set required resource sets
        ArrayList<RequiredResourceSet> requiredResourceSets = new ArrayList<>();
        requiredResourceSets.add(requiredResourceSet);
        this.setRequiredResourceSet(requiredResourceSets);

        this.productions = productions;
        this.requiredValueForProduction = requiredValueForProduction;
    }

    public List<Tuple<RequiredResourceSet, ObtainedResourceSet>> getProductions() {
        return productions;
    }

    public void setProductions(List<Tuple<RequiredResourceSet, ObtainedResourceSet>> productions) {
        this.productions = new ArrayList<>(productions);
    }

    public int getRequiredValueForProduction() {
        return requiredValueForProduction;
    }

    public void setRequiredValueForProduction(int requiredValueForProduction) {
        this.requiredValueForProduction = requiredValueForProduction;
    }
}
