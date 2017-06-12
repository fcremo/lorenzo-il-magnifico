package model.player.bonustile;

import model.resource.ObtainedResourceSet;
import model.resource.RequiredResourceSet;

public class PersonalBonusTile {
    private ObtainedResourceSet productionObtainedResourceSet;
    private RequiredResourceSet productionRequiredResourceSet;
    private ObtainedResourceSet harvestObtainedResourceSet;
    private RequiredResourceSet harvestRequiredResourceSet;

    public PersonalBonusTile(ObtainedResourceSet productionObtainedResourceSet, RequiredResourceSet productionRequiredResourceSet, ObtainedResourceSet harvestObtainedResourceSet, RequiredResourceSet harvestRequiredResourceSet) {
        this.productionObtainedResourceSet = productionObtainedResourceSet;
        this.productionRequiredResourceSet = productionRequiredResourceSet;
        this.harvestObtainedResourceSet = harvestObtainedResourceSet;
        this.harvestRequiredResourceSet = harvestRequiredResourceSet;
    }

    public ObtainedResourceSet getProductionObtainedResourceSet() {
        return productionObtainedResourceSet;
    }

    public void setProductionObtainedResourceSet(ObtainedResourceSet productionObtainedResourceSet) {
        this.productionObtainedResourceSet = productionObtainedResourceSet;
    }

    public RequiredResourceSet getProductionRequiredResourceSet() {
        return productionRequiredResourceSet;
    }

    public void setProductionRequiredResourceSet(RequiredResourceSet productionRequiredResourceSet) {
        this.productionRequiredResourceSet = productionRequiredResourceSet;
    }

    public ObtainedResourceSet getHarvestObtainedResourceSet() {
        return harvestObtainedResourceSet;
    }

    public void setHarvestObtainedResourceSet(ObtainedResourceSet harvestObtainedResourceSet) {
        this.harvestObtainedResourceSet = harvestObtainedResourceSet;
    }

    public RequiredResourceSet getHarvestRequiredResourceSet() {
        return harvestRequiredResourceSet;
    }

    public void setHarvestRequiredResourceSet(RequiredResourceSet harvestRequiredResourceSet) {
        this.harvestRequiredResourceSet = harvestRequiredResourceSet;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof PersonalBonusTile)) return false;

        PersonalBonusTile otherBonusTile = (PersonalBonusTile) o;

        return productionObtainedResourceSet.equals(otherBonusTile.getProductionObtainedResourceSet()) &&
                harvestObtainedResourceSet.equals(otherBonusTile.getHarvestObtainedResourceSet()) &&
                productionRequiredResourceSet.equals(otherBonusTile.getProductionRequiredResourceSet()) &&
                harvestRequiredResourceSet.equals(otherBonusTile.getHarvestRequiredResourceSet());
    }

    @Override
    public String toString() {
        return  "productionObtainedResourceSet=" + productionObtainedResourceSet +
                ", productionRequiredResourceSet=" + productionRequiredResourceSet +
                ", harvestObtainedResourceSet=" + harvestObtainedResourceSet +
                ", harvestRequiredResourceSet=" + harvestRequiredResourceSet;
    }
}