package model.card.development;

import model.action.Action;
import model.resource.RequiredResourceSet;

import java.util.List;

public class VentureCard extends DevelopmentCard {
    public VentureCard(String id, String name, List<RequiredResourceSet> requiredResourceSet, int period,
                       Action immediateAction) {
        super(id, name, requiredResourceSet, period, immediateAction);
    }
}
