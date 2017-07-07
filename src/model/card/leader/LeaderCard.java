package model.card.leader;

import model.card.Card;
import model.resource.RequiredResourceSet;

import java.io.Serializable;
import java.util.List;

public class LeaderCard extends Card implements Serializable {
    public LeaderCard(String id, String name, List<RequiredResourceSet> requiredResourceSet) {
        super(id, name, requiredResourceSet);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(getName());
        string.append("\n");

        if (getRequiredResourceSet() != null && !getRequiredResourceSet().isEmpty()) {
            string.append("requirements: ");
            for (RequiredResourceSet requirement : getRequiredResourceSet()) {
                string.append(requirement)
                      .append("\n")
                      .append("or ");
            }
            if (string.lastIndexOf("or ") != -1) {
                string.delete(string.lastIndexOf("or "), string.lastIndexOf("or ") + 3);
            }
        }

        if (getEffectsContainer() != null && !getEffectsContainer().getEffects().isEmpty()) {
            string.append("effects: ")
                  .append(getEffectsContainer().toString());
        }

        return string.toString();
    }
}
