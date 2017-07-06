package model.player;

import model.resource.ObtainableResourceSet;

import java.io.Serializable;

/**
 * This class represents a personal bonus tile
 */
public class PersonalBonusTile implements Serializable {
    private String id;

    private ObtainableResourceSet productionObtainableResourceSet;
    private ObtainableResourceSet harvestObtainableResourceSet;

    public PersonalBonusTile(ObtainableResourceSet productionObtainableResourceSet, ObtainableResourceSet harvestObtainableResourceSet) {
        this.productionObtainableResourceSet = productionObtainableResourceSet;
        this.harvestObtainableResourceSet = harvestObtainableResourceSet;
    }

    // Empty private constructor to allow automatic deserialization
    private PersonalBonusTile() {
    }

    public ObtainableResourceSet getProductionObtainableResourceSet() {
        return productionObtainableResourceSet;
    }

    public void setProductionObtainableResourceSet(ObtainableResourceSet productionObtainableResourceSet) {
        this.productionObtainableResourceSet = productionObtainableResourceSet;
    }

    public ObtainableResourceSet getHarvestObtainableResourceSet() {
        return harvestObtainableResourceSet;
    }

    public void setHarvestObtainableResourceSet(ObtainableResourceSet harvestObtainableResourceSet) {
        this.harvestObtainableResourceSet = harvestObtainableResourceSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonalBonusTile that = (PersonalBonusTile) o;

        return id.equals(that.id);
    }

    @Override
    public String toString() {
        return "Production: " + productionObtainableResourceSet +
                "\n\tHarvest: " + harvestObtainableResourceSet;
    }
}