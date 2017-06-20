package model.player;

import model.resource.ObtainedResourceSet;

import java.io.Serializable;

/**
 * This class represents a personal bonus tile
 */
public class PersonalBonusTile implements Serializable {
    private ObtainedResourceSet productionObtainedResourceSet;
    private ObtainedResourceSet harvestObtainedResourceSet;

    public PersonalBonusTile(ObtainedResourceSet productionObtainedResourceSet, ObtainedResourceSet harvestObtainedResourceSet) {
        this.productionObtainedResourceSet = productionObtainedResourceSet;
        this.harvestObtainedResourceSet = harvestObtainedResourceSet;
    }

    // Empty private constructor to allow automatic deserialization
    private PersonalBonusTile() {}

    public ObtainedResourceSet getProductionObtainedResourceSet() {
        return productionObtainedResourceSet;
    }

    public void setProductionObtainedResourceSet(ObtainedResourceSet productionObtainedResourceSet) {
        this.productionObtainedResourceSet = productionObtainedResourceSet;
    }

    public ObtainedResourceSet getHarvestObtainedResourceSet() {
        return harvestObtainedResourceSet;
    }

    public void setHarvestObtainedResourceSet(ObtainedResourceSet harvestObtainedResourceSet) {
        this.harvestObtainedResourceSet = harvestObtainedResourceSet;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof PersonalBonusTile)) return false;

        PersonalBonusTile otherBonusTile = (PersonalBonusTile) o;

        return productionObtainedResourceSet.equals(otherBonusTile.getProductionObtainedResourceSet()) &&
                harvestObtainedResourceSet.equals(otherBonusTile.getHarvestObtainedResourceSet());
    }

    @Override
    public String toString() {
        return  "Production: " + productionObtainedResourceSet +
                "\n\tHarvest: " + harvestObtainedResourceSet;
    }
}