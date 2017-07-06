package ui.cli.contexts;

import client.exceptions.NetworkException;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import server.exceptions.ActionNotAllowedException;
import ui.cli.exceptions.InvalidCommandException;
import ui.cli.layout.table.*;

import java.rmi.RemoteException;
import java.util.Optional;

public class MainTurnContext extends Context {
    private Game game;

    private Callback callback;

    public MainTurnContext(PrintInterface printInterface, Game game, Callback callback) {
        super(printInterface);
        this.game = game;
        this.callback = callback;
        this.addCommand("show-board", this::showBoard,
                "Show board status");
        this.addCommand("show-player", this::showPlayer,
                "Show player status");
        this.addCommand("spend-servants", this::spendServants,
                "Commit some servants to spend for the next action");
        this.addCommand("go-to", this::goTo,
                "Go to a position on the board");
        this.addCommand("discard-leader", this::discardLeaderCard,
                "Discard a Leader card from your hand and immediately receive a Council Privilege");
        this.addCommand("play-leader", this::playLeaderCard,
                "Play a Leader Card from your hand");
        this.addCommand("activate-leader", this::activateLeaderCard,
                "Activate a Leader’s Once Per Round Ability");
        printInterface.println("It's your turn!");
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
            String description = String.format("T%d: ", i + 1)
                    + game.getBoard().getTerritoryTower().getFloors().get(i).toString();
            ColumnInterface column;
            if (i == 0) {
                ContainerColumn containerColumn = new ContainerColumn();
                containerColumn.setTitle("GREEN TOWER");
                containerColumn.setDrawBorders(false);
                containerColumn.addRow(new TextBox(description));
                column = containerColumn;
            }
            else {
                column = new TextBox(description);
            }
            ((ContainerRow) layout.getRows().get(i)).addColumn(column);
        }
        for (int i = 0; i < 4; i++) {
            String description = String.format("B%d: ", i + 1)
                    + game.getBoard().getBuildingTower().getFloors().get(i).toString();
            ColumnInterface column;
            if (i == 0) {
                ContainerColumn containerColumn = new ContainerColumn();
                containerColumn.setTitle("BLUE TOWER");
                containerColumn.setDrawBorders(false);
                containerColumn.addRow(new TextBox(description));
                column = containerColumn;
            }
            else {
                column = new TextBox(description);
            }
            ((ContainerRow) layout.getRows().get(i)).addColumn(column);
        }
        for (int i = 0; i < 4; i++) {
            String description = String.format("C%d: ", i + 1)
                    + game.getBoard().getCharacterTower().getFloors().get(i).toString();
            ColumnInterface column;
            if (i == 0) {
                ContainerColumn containerColumn = new ContainerColumn();
                containerColumn.setTitle("YELLOW TOWER");
                containerColumn.setDrawBorders(false);
                containerColumn.addRow(new TextBox(description));
                column = containerColumn;
            }
            else {
                column = new TextBox(description);
            }
            ((ContainerRow) layout.getRows().get(i)).addColumn(column);
        }
        for (int i = 0; i < 4; i++) {
            String description = String.format("V%d: ", i + 1)
                    + game.getBoard().getVentureTower().getFloors().get(i).toString();
            ColumnInterface column;
            if (i == 0) {
                ContainerColumn containerColumn = new ContainerColumn();
                containerColumn.setTitle("PURPLE TOWER");
                containerColumn.setDrawBorders(false);
                containerColumn.addRow(new TextBox(description));
                column = containerColumn;
            }
            else {
                column = new TextBox(description);
            }
            ((ContainerRow) layout.getRows().get(i)).addColumn(column);
        }

        ContainerRow lastRow = new ContainerRow();
        layout.addRow(lastRow);

        // Production Area
        ContainerColumn productionAreaColumn = new ContainerColumn();
        productionAreaColumn.setTitle("PRODUCTION");
        lastRow.addColumn(productionAreaColumn);

        String mainProductionArea = "Main production area:\n"
                + game.getBoard().getSmallProductionArea().getOccupantsString();
        productionAreaColumn.addRow(new TextBox(mainProductionArea));

        if (game.getPlayers().size() > 2) {
            String secondaryProductionArea = "Secondary production area:\n"
                    + game.getBoard().getBigProductionArea().getOccupantsString();
            productionAreaColumn.addRow(new TextBox(secondaryProductionArea));
        }

        // Harvest Area
        ContainerColumn harvestAreaColumn = new ContainerColumn();
        harvestAreaColumn.setTitle("HARVEST");
        lastRow.addColumn(harvestAreaColumn);

        String mainHarvestArea = "Main harvest area:\n"
                + game.getBoard().getSmallHarvestArea().getOccupantsString();
        harvestAreaColumn.addRow(new TextBox(mainHarvestArea));

        if (game.getPlayers().size() > 2) {
            String secondaryHarvestArea = "Secondary harvest area:\n"
                    + game.getBoard().getBigHarvestArea().getOccupantsString();
            productionAreaColumn.addRow(new TextBox(secondaryHarvestArea));
        }

        // Market
        ContainerColumn marketColumn = new ContainerColumn();
        marketColumn.setTitle("MARKET");
        lastRow.addColumn(marketColumn);

        String market1 = String.format("M1 : ")
                + game.getBoard().getMarket1().toString();
        marketColumn.addRow(new TextBox(market1));

        String market2 = String.format("M2 : ")
                + game.getBoard().getMarket2().toString();
        marketColumn.addRow(new TextBox(market2));

        if (game.getPlayers().size() > 2) {
            String market3 = String.format("M3 : ")
                    + game.getBoard().getMarket3().toString();
            marketColumn.addRow(new TextBox(market3));

            if (game.getPlayers().size() > 3) {
                String market4 = String.format("M4 : ")
                        + game.getBoard().getMarket4().toString();
                marketColumn.addRow(new TextBox(market4));
            }
        }

        // Council Palace
        ContainerColumn councilPalaceColumn = new ContainerColumn();
        councilPalaceColumn.setTitle("COUNCIL PALACE");
        lastRow.addColumn(councilPalaceColumn);

        String councilPalaceArea = "Council palace:\n";
        councilPalaceArea += "Gives: " + game.getBoard().getCouncilPalace().getBonus() + "\n"
                + game.getBoard().getCouncilPalace().getOccupantsString();
        councilPalaceColumn.addRow(new TextBox(councilPalaceArea));

        // Dice values
        StringBuilder dice = new StringBuilder();
        dice.append("Black Die: ").append(game.getBlackDie()).append("\n")
            .append("White Die: ").append(game.getWhiteDie()).append("\n")
            .append("Orange Die: ").append(game.getOrangeDie());
        councilPalaceColumn.addRow(new TextBox(dice.toString()));

        printer.printLayout(layout);
    }

    private void showPlayer(String[] params) throws InvalidCommandException {
        if (params.length != 1) {
            throw new InvalidCommandException("This command takes one argument (the player's username)");
        }

        Optional<Player> optPlayer = game.getPlayers().stream()
                                         .filter(p -> p.getUsername().equals(params[0]))
                                         .findFirst();

        if (!optPlayer.isPresent()) {
            throw new InvalidCommandException("Wrong username");
        }

        Player player = optPlayer.get();


        TableLayout layout = new TableLayout();

        // Player's name
        layout.addRow(new ContainerRow());
        ((ContainerRow) layout.getRows().get(0)).addColumn(new TextBox(player.getUsername()));

        // Territory cards
        ContainerRow territories = new ContainerRow();
        layout.addRow(territories);
        for (int i = 0; i < player.getTerritories().size(); i++) {
           territories.addColumn(new TextBox(player.getTerritories().get(i).toString()));
        }
        for (int i = player.getTerritories().size(); i < 6; i++) {
            territories.addColumn(new TextBox(""));
        }

        // Building cards
        ContainerRow buildings = new ContainerRow();
        layout.addRow(buildings);
        for (int i = 0; i < player.getBuildings().size(); i++) {
            buildings.addColumn(new TextBox(player.getBuildings().get(i).toString()));
        }
        for (int i = player.getBuildings().size(); i < 6; i++) {
            buildings.addColumn(new TextBox(""));
        }

        // Character cards
        ContainerRow characters = new ContainerRow();
        layout.addRow(characters);
        for (int i = 0; i < player.getCharacters().size(); i++) {
            characters.addColumn(new TextBox(player.getCharacters().get(i).toString()));
        }
        for (int i = player.getCharacters().size(); i < 6; i++) {
            characters.addColumn(new TextBox(""));
        }

        //Venture cards
        ContainerRow ventures = new ContainerRow();
        layout.addRow(ventures);
        for (int i = 0; i < player.getVentures().size(); i++) {
            ventures.addColumn(new TextBox(player.getVentures().get(i).toString()));
        }
        for (int i = player.getVentures().size(); i < 6; i++) {
            ventures.addColumn(new TextBox(""));
        }

        // Leader cards
        ContainerRow lastRow = new ContainerRow();
        layout.addRow(lastRow);

        for (int i = 0; i < player.getPlayedLeaderCards().size(); i++) {
            String description = player.getPlayedLeaderCards().get(i).toString() + "\n ALREADY PLAYED";
            lastRow.addColumn(new TextBox(description));
        }
        for (int i = 0; i < player.getAvailableLeaderCards().size(); i++) {
            String description = player.getAvailableLeaderCards().get(i).toString() + "\n NOT PLAYED YET";
            lastRow.addColumn(new TextBox(description));
        }

        // Personal bonus tile
        ContainerColumn tileColumn = new ContainerColumn();
        lastRow.addColumn(tileColumn);

        String production = "PRODUCTION: " + player.getBonusTile().getProductionObtainedResourceSet().toString();
        tileColumn.addRow(new TextBox(production));
        String harvest = "HARVEST: " + player.getBonusTile().getHarvestObtainedResourceSet().toString();
        tileColumn.addRow(new TextBox(harvest));

        // Resources
        String description = player.getResources().toString();
        lastRow.addColumn(new TextBox(description));

        printer.printLayout(layout);

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

    private void goTo(String[] params) throws InvalidCommandException, NetworkException, RemoteException {
        if (params.length != 2) {
            throw new InvalidCommandException("This command takes two arguments " +
                    "(the action space you want to go to and the color of the family member you want to use)");
        }

        ActionSpace actionSpace = null;
        FamilyMemberColor familyMember = null;

        switch (params[0].toLowerCase()) {
            case "t1":
                actionSpace = game.getBoard().getTerritoryTower().getFloors().get(1);
                break;
            case "t2":
                actionSpace = game.getBoard().getTerritoryTower().getFloors().get(2);
                break;
            case "t3":
                actionSpace = game.getBoard().getTerritoryTower().getFloors().get(3);
                break;
            case "t4":
                actionSpace = game.getBoard().getTerritoryTower().getFloors().get(4);
                break;
            case "c1":
                actionSpace = game.getBoard().getCharacterTower().getFloors().get(1);
                break;
            case "c2":
                actionSpace = game.getBoard().getCharacterTower().getFloors().get(2);
                break;
            case "c3":
                actionSpace = game.getBoard().getCharacterTower().getFloors().get(3);
                break;
            case "c4":
                actionSpace = game.getBoard().getCharacterTower().getFloors().get(4);
                break;
            case "b1":
                actionSpace = game.getBoard().getBuildingTower().getFloors().get(1);
                break;
            case "b2":
                actionSpace = game.getBoard().getBuildingTower().getFloors().get(2);
                break;
            case "b3":
                actionSpace = game.getBoard().getBuildingTower().getFloors().get(3);
                break;
            case "b4":
                actionSpace = game.getBoard().getBuildingTower().getFloors().get(4);
                break;
            case "v1":
                actionSpace = game.getBoard().getVentureTower().getFloors().get(1);
                break;
            case "v2":
                actionSpace = game.getBoard().getVentureTower().getFloors().get(2);
                break;
            case "v3":
                actionSpace = game.getBoard().getVentureTower().getFloors().get(3);
                break;
            case "v4":
                actionSpace = game.getBoard().getVentureTower().getFloors().get(4);
                break;
            case "m1":
                actionSpace = game.getBoard().getMarket1();
                break;
            case "m2":
                actionSpace = game.getBoard().getMarket2();
                break;
            case "m3":
                actionSpace = game.getBoard().getMarket3();
                break;
            case "m4":
                actionSpace = game.getBoard().getMarket4();
                break;
            case "production":
                if (game.getBoard().getSmallProductionArea().getOccupants().isEmpty()) {
                    actionSpace = game.getBoard().getSmallProductionArea();
                }
                else {
                    actionSpace = game.getBoard().getBigProductionArea();
                }
                break;
            case "harvest":
                if (game.getBoard().getSmallHarvestArea().getOccupants().isEmpty()) {
                    actionSpace = game.getBoard().getSmallHarvestArea();
                }
                else {
                    actionSpace = game.getBoard().getBigHarvestArea();
                }
                break;
            case "council":
                actionSpace = game.getBoard().getCouncilPalace();
                break;
            default:
                throw new InvalidCommandException("Invalid Action Space");
        }

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
                throw new InvalidCommandException("Invalid Family Member Color");
        }

        try {
            callback.goToActionSpace(actionSpace, familyMember);
        }
        catch (ActionNotAllowedException e) {
            printer.println("You cannot go there!");
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

        void goToActionSpace(ActionSpace actionSpace, FamilyMemberColor familyMember) throws NetworkException, RemoteException, ActionNotAllowedException;

        void discardLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException;

        void playLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException;

        void activateLeaderCard(LeaderCard leaderCard) throws NetworkException, RemoteException, ActionNotAllowedException;

    }
}
