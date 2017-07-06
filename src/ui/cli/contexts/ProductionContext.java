package ui.cli.contexts;

import client.exceptions.NetworkException;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.Card;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainableResource;
import model.resource.RequiredResourceSet;
import server.exceptions.ActionNotAllowedException;
import ui.cli.exceptions.InvalidCommandException;
import ui.cli.layout.table.ContainerColumn;
import ui.cli.layout.table.ContainerRow;
import ui.cli.layout.table.TableLayout;
import ui.cli.layout.table.TextBox;

import java.rmi.RemoteException;
import java.util.Optional;
import java.util.StringJoiner;

public class ProductionContext extends Context {
    private Game game;
    private int productionValue;

    private Callback callback;

    public ProductionContext(PrintInterface printInterface, int productionValue, Game game, Callback callback) {
        super(printInterface);
        this.game = game;
        this.callback = callback;
        this.addCommand("show-board", this::showBoard, "Show board status");
        this.addCommand("show-player", this::showPlayer, "Show player status");
        this.addCommand("show-productions", this::showPlayer, "Show productions");
        this.addCommand("spend-servants", this::spendServants, "Commit some servants to spend for the next action");
        printInterface.println("You can perform a production of value " + productionValue);
    }

    private void showBoard(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");

        TableLayout layout = new TableLayout();

        // Add 4 rows for the floors
        layout.addRow(new ContainerRow());
        layout.addRow(new ContainerRow());
        layout.addRow(new ContainerRow());
        layout.addRow(new ContainerRow());

        for (int i = 0; i < 4; i++) {
            TerritoryCard territoryCard = game.getBoard().getTerritoryTower().getCard(i);
            String description = String.format("T%d: ", i) + territoryCard;
            ((ContainerRow) layout.getRows().get(i)).addColumn(new TextBox(description));
        }
        for (int i = 0; i < 4; i++) {
            CharacterCard characterCard = game.getBoard().getCharacterTower().getCard(i);
            String description = String.format("C%d: ", i) + characterCard;
            ((ContainerRow) layout.getRows().get(i)).addColumn(new TextBox(description));
        }
        for (int i = 0; i < 4; i++) {
            BuildingCard buildingCard = game.getBoard().getBuildingTower().getCard(i);
            String description = String.format("B%d: ", i) + buildingCard;
            ((ContainerRow) layout.getRows().get(i)).addColumn(new TextBox(description));
        }
        for (int i = 0; i < 4; i++) {
            VentureCard ventureCard = game.getBoard().getVentureTower().getCard(i);
            String description = String.format("V%d: ", i) + ventureCard;
            ((ContainerRow) layout.getRows().get(i)).addColumn(new TextBox(description));
        }

        ContainerRow lastRow = new ContainerRow();
        layout.addRow(lastRow);

        // Production Area
        ContainerColumn productionAreaColumn = new ContainerColumn();
        lastRow.addColumn(productionAreaColumn);

        String mainProductionArea = "Main production area:\n";
        if (!game.getBoard().getSmallProductionArea().getOccupants().isEmpty()) {
            mainProductionArea += "Occupied by " + game.getBoard().getSmallProductionArea().getOccupants().toString();
        }
        else {
            mainProductionArea += "Is not occupied yet";
        }
        productionAreaColumn.addRow(new TextBox(mainProductionArea));

        if (game.getPlayers().size() > 2) {
            String secondaryProductionArea = "Secondary production area:\n";
            if (!game.getBoard().getBigProductionArea().getOccupants().isEmpty()) {
                secondaryProductionArea += "Occupied by " + game.getBoard().getBigProductionArea().getOccupants().toString();
            }
            else {
                secondaryProductionArea += "Is not occupied yet";
            }
            productionAreaColumn.addRow(new TextBox(secondaryProductionArea));
        }

        // Harvest Area
        ContainerColumn harvestAreaColumn = new ContainerColumn();
        lastRow.addColumn(harvestAreaColumn);

        String mainHarvestArea = "Main harvest area:\n";
        if (!game.getBoard().getSmallHarvestArea().getOccupants().isEmpty()) {
            mainHarvestArea += "Occupied by " + game.getBoard().getSmallHarvestArea().getOccupants().toString();
        }
        else {
            mainHarvestArea += "Is not occupied yet";
        }
        harvestAreaColumn.addRow(new TextBox(mainHarvestArea));

        if (game.getPlayers().size() > 2) {
            String secondaryHarvestArea = "Secondary harvest area:\n";
            if (!game.getBoard().getBigHarvestArea().getOccupants().isEmpty()) {
                secondaryHarvestArea += "Occupied by " + game.getBoard().getBigHarvestArea().getOccupants().toString();
            }
            else {
                secondaryHarvestArea += "Is not occupied yet";
            }
            productionAreaColumn.addRow(new TextBox(secondaryHarvestArea));
        }

        // Council Palace
        ContainerColumn councilPalaceColumn = new ContainerColumn();
        lastRow.addColumn(councilPalaceColumn);

        String councilPalaceArea = "Council palace:\n";
        councilPalaceArea += "Gives: " + game.getBoard().getCouncilPalace().getBonus() + "\n";
        if (!game.getBoard().getCouncilPalace().getOccupants().isEmpty()) {
            councilPalaceArea += "Occupied by " + game.getBoard().getSmallHarvestArea().getOccupants().toString();
        }
        else {
            councilPalaceArea += "Is not occupied yet";
        }
        councilPalaceColumn.addRow(new TextBox(councilPalaceArea));

        printer.printLayout(layout);

        // Market
        printer.println("Market:");

        printer.println("M1 Gives: " + game.getBoard().getMarket1().getBonus());
        if (!game.getBoard().getMarket1().getOccupants().isEmpty()) {
            printer.println(("Occupied by " + game.getBoard().getMarket1().getOccupants().toString()));
        }
        else {
            printer.println("Is not occupied yet");
        }

        printer.println("M2 Gives: " + game.getBoard().getMarket2().getBonus());
        if (!game.getBoard().getMarket2().getOccupants().isEmpty()) {
            printer.println(("Occupied by " + game.getBoard().getMarket2().getOccupants().toString()));
        }
        else {
            printer.println("Is not occupied yet");
        }

        if (game.getPlayers().size() > 2) {
            printer.println("M3 Gives: " + game.getBoard().getMarket3().getBonus());
            if (!game.getBoard().getMarket3().getOccupants().isEmpty()) {
                printer.println(("Occupied by " + game.getBoard().getMarket3().getOccupants().toString()));
            }
        }
        else {
            printer.println("Is not occupied yet");
        }

        if (game.getPlayers().size() > 3) {
            printer.println("M4 Gives: " + game.getBoard().getMarket4().getBonus());
            if (!game.getBoard().getMarket4().getOccupants().isEmpty()) {
                printer.println(("Occupied by " + game.getBoard().getMarket4().getOccupants().toString()));
            }
            else {
                printer.println("Is not occupied yet");
            }
        }


        // Dice values
        printer.println("Black Die: " + game.getBlackDie());
        printer.println("White Die: " + game.getWhiteDie());
        printer.println("Orange Die: " + game.getOrangeDie());

        printer.printPrompt();
    }

    private void showPlayer(String[] params) throws InvalidCommandException {
        if (params.length != 1) {
            throw new InvalidCommandException("This command takes one argument (the username of the player)");
        }

        Optional<Player> optPlayer = game.getPlayers().stream()
                                         .filter(p -> p.getUsername().equals(params[0]))
                                         .findFirst();

        if (!optPlayer.isPresent()) {
            throw new InvalidCommandException("Wrong username");
        }

        Player player = optPlayer.get();

        StringBuilder sb = new StringBuilder();
        sb.append(player.getUsername())
          .append("\n");

        sb.append("Resources:\n");
        StringJoiner sj = new StringJoiner(", ");
        for (ObtainableResource resource : player.getResources().keySet()) {
            sj.add(resource + " : " + player.getResources().get(resource));
        }

        sb.append("Territories:\n");
        for (TerritoryCard territoryCard : player.getTerritories()) {
            sb.append(territoryCard);
            sb.append("\n");
        }
        sb.append("Characters:\n");
        for (CharacterCard characterCard : player.getCharacters()) {
            sb.append(characterCard);
            sb.append("\n");
        }
        sb.append("Buildings:\n");
        for (BuildingCard buildingCard : player.getBuildings()) {
            sb.append(buildingCard);
            sb.append("\n");
        }
        sb.append("Ventures:\n");
        for (VentureCard ventureCard : player.getVentures()) {
            sb.append(ventureCard);
            sb.append("\n");
        }

        sb.append("Leader Cards\n");
        sb.append("Activated Leader Cards: \n");
        int i = 1;
        for (LeaderCard leaderCard : player.getPlayedLeaderCards()) {
            sb.append(String.format("%d) ", i));
            sb.append(leaderCard);
            sb.append("\n");
            i++;
        }
        sb.append("Not yet activated Leader Cards: \n");
        i = 1;
        for (LeaderCard leaderCard : player.getAvailableLeaderCards()) {
            sb.append(String.format("%d) ", i));
            sb.append(leaderCard);
            sb.append("\n");
            i++;
        }

        printer.println(sb.toString());
    }

    private void spendServants(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 1) {
            throw new InvalidCommandException("This command takes one argument (the number of servants you want to use)");
        }

        try {
            int servants = Integer.parseInt(params[0]);
            callback.spendServants(servants);

        }
        catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid number of servants");
        }
        catch (ActionNotAllowedException e) {
            printer.println("You cannot do that", true);
        }
    }

    private void goTo(String[] params) throws InvalidCommandException, NetworkException, RemoteException, ActionNotAllowedException {
        if (params.length != 2) {
            throw new InvalidCommandException("This command takes two arguments " +
                    "(the action space you want to go to and the color of the family member you want to use)");
        }

        FamilyMemberColor familyMember = null;
        switch (params[1].toLowerCase()) {
            case "black":
                familyMember = FamilyMemberColor.BLACK;
                break;
            case "white":
                familyMember = FamilyMemberColor.WHITE;
                break;
            case "orange":
                familyMember = FamilyMemberColor.ORANGE;
                break;
            case "neutral":
                familyMember = FamilyMemberColor.NEUTRAL;
                break;
            default:
                throw new InvalidCommandException("Invalid family member color");
        }

        String place = params[0].toLowerCase();

        if(place.equals("council")) {
            callback.goToCouncilPalace(familyMember);
        }
        else if (place.equals("harvest")) {
            if (game.getBoard().getSmallHarvestArea().getOccupants().isEmpty()) {
                callback.goToSmallHarvest(familyMember);
            }
            else {
                callback.goToBigHarvest(familyMember);
            }
        }
        else if (place.equals("production")) {
            if (game.getBoard().getSmallProductionArea().getOccupants().isEmpty()) {
                callback.goToSmallProduction(familyMember);
            }
            else {
                callback.goToBigProduction(familyMember);
            }
        }
        else if (place.matches("[tcbv][1234]")) {
            int index = Integer.parseInt(place.substring(1,2));
            if(index < 1 || index > 4) throw new InvalidCommandException("Invalid floor index");

            Floor floor;

            switch(place.substring(0, 1)){
                case "t":
                    game.getBoard().getTerritoryTower().getFloors().get(index);
                    break;
                case "c":
                    game.getBoard().getCharacterTower().getFloors().get(index);
                    break;
                case "b":
                    game.getBoard().getBuildingTower().getFloors().get(index);
                    break;
                case "v":
                    game.getBoard().getVentureTower().getFloors().get(index);
                    break;
            }

            callback.goToFloor(floor, familyMember);
        }
        else if (place.matches("m[1234]")) {
            int index = Integer.parseInt(place.substring(1,2));
            if(index == 1) {
                callback.goToMarket(familyMember, game.getBoard().getMarket1());
            }
            else if (index == 2) {
                callback.goToMarket(familyMember, game.getBoard().getMarket2());
            }
            else if (index == 3) {
                callback.goToMarket(familyMember, game.getBoard().getMarket3());
            }
            else if (index == 4) {
                callback.goToMarket(familyMember, game.getBoard().getMarket4());
            }
            else throw new InvalidCommandException("Invalid market index");
        }
        else {
            throw new InvalidCommandException("Invalid action space");
        }
    }

    private void discardLeaderCard(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 1) {
            throw new InvalidCommandException("This command takes one argument (the index of the Leader Card you want to discard)");
        }

        try {
            int i = Integer.parseInt(params[0]);
            LeaderCard leaderCard = game.getCurrentPlayer().getAvailableLeaderCards().get(i - 1);
            callback.discardLeaderCard(leaderCard);
        }
        catch (NumberFormatException | IndexOutOfBoundsException e) {
            printer.println("Invalid Leader Card");
        }
        catch (ActionNotAllowedException e) {
            printer.println("You cannot discard this Leader Card");
        }
    }

    private void playLeaderCard(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 1) {
            throw new InvalidCommandException("This command takes one argument (the Leader Card you want to play)");
        }

        try {
            int i = Integer.parseInt(params[0]);
            LeaderCard leaderCard = game.getCurrentPlayer().getAvailableLeaderCards().get(i - 1);
            callback.playLeaderCard(leaderCard);
        }
        catch (NumberFormatException | IndexOutOfBoundsException e) {
            printer.println("Invalid Leader Card");
        }
        catch (ActionNotAllowedException e) {
            printer.println("You cannot play this Leader Card");
        }
    }

    private void activateLeaderCard(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 1) {
            throw new InvalidCommandException("This command takes one argument (the Leader Card you want to activate)");
        }

        try {
            int i = Integer.parseInt(params[0]);
            LeaderCard leaderCard = game.getCurrentPlayer().getAvailableLeaderCards().get(i - 1);
            callback.activateLeaderCard(leaderCard);
        }
        catch (NumberFormatException | IndexOutOfBoundsException e) {
            printer.println("Invalid Leader Card");
        }
        catch (ActionNotAllowedException e) {
            printer.println("You cannot activate this Leader Card");
        }
    }

    public interface Callback {
        void spendServants(int servants) throws NetworkException, RemoteException, ActionNotAllowedException;

        void goToFloor(Floor floor, FamilyMemberColor familyMember, RequiredResourceSet paymentForCard) throws NetworkException, RemoteException, ActionNotAllowedException;

        void goToSmallHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

        void goToBigHarvest(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

        void goToSmallProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

        void goToBigProduction(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

        void goToCouncilPalace(FamilyMemberColor familyMemberColor) throws NetworkException, RemoteException, ActionNotAllowedException;

        void goToMarket(FamilyMemberColor familyMemberColor, ActionSpace marketActionSpace) throws NetworkException, RemoteException, ActionNotAllowedException;

        void discardLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException;

        void playLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException;

        void activateOncePerRoundEffect(Card Card, OncePerRoundEffectInterface effect) throws NetworkException, RemoteException, ActionNotAllowedException;
    }
}
