package model.board;

import model.Excommunication;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.resource.ObtainedResourceSet;

import java.io.Serializable;

/**
 * This class represents the state of the board.
 */
public class Board implements Serializable {
    private Game game;
    private ActionSpace councilPalace;
    private ActionSpace smallProductionArea;
    private ActionSpace bigProductionArea;
    private ActionSpace smallHarvestArea;
    private ActionSpace bigHarvestArea;
    private ActionSpace market1;
    private ActionSpace market2;
    private ActionSpace market3;
    private ActionSpace market4;
    private Tower<TerritoryCard> territoryTower = new Tower<>();
    private Tower<CharacterCard> characterTower = new Tower<>();
    private Tower<BuildingCard> buildingTower = new Tower<>();
    private Tower<VentureCard> ventureTower = new Tower<>();
    private Excommunication[] excommunications = new Excommunication[3];
    private ObtainedResourceSet[] faithTrackBonus = new ObtainedResourceSet[16];

    public Board(Game game) {
        this.game = game;
    }

    public ActionSpace getSmallProductionArea() {
        return smallProductionArea;
    }

    public void setSmallProductionArea(ActionSpace smallProductionArea) {
        this.smallProductionArea = smallProductionArea;
    }

    public ActionSpace getSmallHarvestArea() {
        return smallHarvestArea;
    }

    public void setSmallHarvestArea(ActionSpace smallHarvestArea) {
        this.smallHarvestArea = smallHarvestArea;
    }

    public ActionSpace getCouncilPalace() {
        return councilPalace;
    }

    public void setCouncilPalace(ActionSpace councilPalace) {
        this.councilPalace = councilPalace;
    }

    public ActionSpace getBigProductionArea() {
        return bigProductionArea;
    }

    public void setBigProductionArea(ActionSpace bigProductionArea) {
        this.bigProductionArea = bigProductionArea;
    }

    public ActionSpace getBigHarvestArea() {
        return bigHarvestArea;
    }

    public void setBigHarvestArea(ActionSpace bigHarvestArea) {
        this.bigHarvestArea = bigHarvestArea;
    }

    public ActionSpace getMarket1() {
        return market1;
    }

    public void setMarket1(ActionSpace market1) {
        this.market1 = market1;
    }

    public ActionSpace getMarket2() {
        return market2;
    }

    public void setMarket2(ActionSpace market2) {
        this.market2 = market2;
    }

    public ActionSpace getMarket3() {
        return market3;
    }

    public void setMarket3(ActionSpace market3) {
        this.market3 = market3;
    }

    public ActionSpace getMarket4() {
        return market4;
    }

    public void setMarket4(ActionSpace market4) {
        this.market4 = market4;
    }

    public Tower<TerritoryCard> getTerritoryTower() {
        return territoryTower;
    }

    public void setTerritoryTower(Tower<TerritoryCard> territoryTower) {
        this.territoryTower = territoryTower;
    }

    public Tower<CharacterCard> getCharacterTower() {
        return characterTower;
    }

    public void setCharacterTower(Tower<CharacterCard> characterTower) {
        this.characterTower = characterTower;
    }

    public Tower<BuildingCard> getBuildingTower() {
        return buildingTower;
    }

    public void setBuildingTower(Tower<BuildingCard> buildingTower) {
        this.buildingTower = buildingTower;
    }

    public Tower<VentureCard> getVentureTower() {
        return ventureTower;
    }

    public void setVentureTower(Tower<VentureCard> ventureTower) {
        this.ventureTower = ventureTower;
    }

    public Excommunication[] getExcommunications() {
        return excommunications;
    }

    public void setExcommunications(Excommunication[] excommunications) {
        this.excommunications = excommunications;
    }

    public ObtainedResourceSet[] getFaithTrackBonus() {
        return faithTrackBonus;
    }

    public void setFaithTrackBonus(ObtainedResourceSet[] faithTrackBonus) {
        this.faithTrackBonus = faithTrackBonus;
    }
}
