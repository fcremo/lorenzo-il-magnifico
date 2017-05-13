package model.card.development;

import model.action.Action;
import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class BuildingCard extends DevelopmentCard {
    private ArrayList<Tuple<RequiredResourceSet, ObtainedResourceSet>> productions;
    private int requiredValueForProduction;

    public BuildingCard(String id, String name, List<RequiredResourceSet> requiredResourceSet, int period,
                        Action immediateAction, ArrayList<Tuple<RequiredResourceSet, ObtainedResourceSet>> productions,
                        int requiredValueForProduction) {
        super(id, name, requiredResourceSet, period, immediateAction);
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
