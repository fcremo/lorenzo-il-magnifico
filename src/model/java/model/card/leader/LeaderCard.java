package model.card.leader;

import model.card.Card;
import model.resource.RequiredResourceSet;

import java.util.List;

public class LeaderCard extends Card {
    public LeaderCard(String id, String name, List<RequiredResourceSet> requiredResourceSet) {
        super(id, name, requiredResourceSet);
    }
}
