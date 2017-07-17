package model.card.development;

import model.action.ActionType;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import model.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class BuildingCard extends DevelopmentCard {
    /**
     * The possible conversions the card can do when activated during production
     * The first element of the tuple represents a cost/requirement,
     * the second what's obtained when satisfying the cost.
     */
    private ArrayList<Tuple<RequiredResourceSet, ObtainableResourceSet>> productions;

    /**
     * The value required for activating the card during production
     */
    private int requiredValueForProduction;

    /**
     * True if this card has been activated during harvest
     */
    private boolean hasBeenActivated;

    public BuildingCard(String name, RequiredResourceSet requiredResourceSet, int period,
                        List<Tuple<RequiredResourceSet, ObtainableResourceSet>> productions,
                        int requiredValueForProduction) {
        super(name, new ArrayList<>(), period);

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
        StringJoiner sj = new StringJoiner("\n");
        sj.add(getName());

        if (getRequiredResourceSet() != null && !getRequiredResourceSet().isEmpty()) {
            StringJoiner sj2 = new StringJoiner("\n or ");
            sj2.add("Price: ");
            for (RequiredResourceSet requirement : getRequiredResourceSet()) {
                sj2.add(requirement.toString());
            }
            sj.add(sj2.toString());
        }

        if (getEffectsContainer() != null && !getEffectsContainer().getEffects().isEmpty()) {
            sj.add("Effects: "+ getEffectsContainer().toString());
        }

        if (!getProductions().isEmpty()) {

            String head = String.format("Production (at %d): ", getRequiredValueForProduction());

            StringJoiner sj2 = new StringJoiner("\n or ");
            for(Tuple<RequiredResourceSet, ObtainableResourceSet> production : getProductions()) {
                RequiredResourceSet required = production.first;
                ObtainableResourceSet obtained = production.second;

                String tmp = obtained.toString();

                if (required != null && !required.isEmpty()) {
                    tmp += " for " + required;
                }
                sj2.add(tmp);
            }

            sj.add(head + sj2.toString());
        }

        return sj.toString();
    }

    @Override
    public ActionType getCardTakingActionType() {
        return ActionType.TAKE_BUILDING_CARD;
    }

    public boolean hasBeenActivated() {
        return hasBeenActivated;
    }

    public void setActivated(boolean hasBeenActivated) {
        this.hasBeenActivated = hasBeenActivated;
    }
}
