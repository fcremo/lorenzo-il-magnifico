package model.board;

import model.Excommunication;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.resource.ObtainedResourceSet;

/**
 * This class represents the state of the board.
 */
public class Board {
    private Game game;
    private ActionSpace councilPalace;
    private ActionSpace smallProductionArea;
    private ActionSpace bigProductionArea;
    private ActionSpace smallHarvestArea;
    private ActionSpace bigHarvestArea;
    private ActionSpace marketGold;
    private ActionSpace marketServants;
    private ActionSpace marketMilitaryAndGold;
    private ActionSpace marketCouncilPrivileges;
    private Tower<TerritoryCard> greenTower = new Tower<>();
    private Tower<CharacterCard> blueTower = new Tower<>();
    private Tower<BuildingCard> yellowTower = new Tower<>();
    private Tower<VentureCard> purpleTower = new Tower<>();
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

    public ActionSpace getMarketGold() {
        return marketGold;
    }

    public void setMarketGold(ActionSpace marketGold) {
        this.marketGold = marketGold;
    }

    public ActionSpace getMarketServants() {
        return marketServants;
    }

    public void setMarketServants(ActionSpace marketServants) {
        this.marketServants = marketServants;
    }

    public ActionSpace getMarketMilitaryAndGold() {
        return marketMilitaryAndGold;
    }

    public void setMarketMilitaryAndGold(ActionSpace marketMilitaryAndGold) {
        this.marketMilitaryAndGold = marketMilitaryAndGold;
    }

    public ActionSpace getMarketCouncilPrivileges() {
        return marketCouncilPrivileges;
    }

    public void setMarketCouncilPrivileges(ActionSpace marketCouncilPrivileges) {
        this.marketCouncilPrivileges = marketCouncilPrivileges;
    }

    public Tower<TerritoryCard> getGreenTower() {
        return greenTower;
    }

    public void setGreenTower(Tower<TerritoryCard> greenTower) {
        this.greenTower = greenTower;
    }

    public Tower<CharacterCard> getBlueTower() {
        return blueTower;
    }

    public void setBlueTower(Tower<CharacterCard> blueTower) {
        this.blueTower = blueTower;
    }

    public Tower<BuildingCard> getYellowTower() {
        return yellowTower;
    }

    public void setYellowTower(Tower<BuildingCard> yellowTower) {
        this.yellowTower = yellowTower;
    }

    public Tower<VentureCard> getPurpleTower() {
        return purpleTower;
    }

    public void setPurpleTower(Tower<VentureCard> purpleTower) {
        this.purpleTower = purpleTower;
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
