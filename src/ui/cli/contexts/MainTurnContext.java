package ui.cli.contexts;

import client.exceptions.NetworkException;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.card.development.BuildingCard;
import model.card.development.CharacterCard;
import model.card.development.TerritoryCard;
import model.card.development.VentureCard;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainableResource;
import server.exceptions.ActionNotAllowedException;
import ui.cli.exceptions.InvalidCommandException;
import ui.cli.layout.table.ContainerColumn;
import ui.cli.layout.table.ContainerRow;
import ui.cli.layout.table.TableLayout;
import ui.cli.layout.table.TextBox;

import java.rmi.RemoteException;
import java.util.Optional;
import java.util.StringJoiner;

public class MainTurnContext extends Context {
    private Game game;

    private Callback callback;

    public MainTurnContext(PrintInterface printInterface, Game game, Callback callback) {
        super(printInterface);
        this.game = game;
        this.callback = callback;
        this.addCommand("show-board", this::showBoard, "Show board status");
        this.addCommand("show-player", this::showPlayer, "Show player status");
        this.addCommand("spend-servants", this::spendServants, "Commit some servants to spend for the next action");
        this.addCommand("go-to", this::goTo, "Go to a position on the board");
        this.addCommand("discard-leader", this::discardLeaderCard, "Discard a Leader card from your hand and immediately receive a Council Privilege");
        this.addCommand("play-leader", this::playLeaderCard, "Play a Leader Card from your hand");
        this.addCommand("activate-leader", this::activateLeaderCard, "Activate a Leader’s Once Per Round Ability");
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
