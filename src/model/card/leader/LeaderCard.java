package model.card.leader;

import model.card.Card;
import model.resource.RequiredResourceSet;

import java.io.Serializable;
import java.util.List;

public class LeaderCard extends Card implements Serializable {
    public LeaderCard(String id, String name, List<RequiredResourceSet> requiredResourceSet) {
        super(id, name, requiredResourceSet);
    }
}
