package model.card.development;

import model.resource.RequiredResourceSet;

import java.util.List;

public class VentureCard extends DevelopmentCard {
    public VentureCard(String id, String name, List<RequiredResourceSet> requiredResourceSet, int period) {
        super(id, name, requiredResourceSet, period);
    }
}
