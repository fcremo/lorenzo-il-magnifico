package ui.cli.contexts;

import gamecontroller.GameController;
import gamecontroller.exceptions.ActionNotAllowedException;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.card.Card;
import model.card.development.DevelopmentCard;
import model.card.effects.interfaces.OncePerRoundEffectInterface;
import model.card.leader.LeaderCard;
import model.player.FamilyMemberColor;
import model.player.Player;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import ui.cli.exceptions.InvalidCommandException;
import ui.cli.layout.table.*;

import java.util.*;

public class MainTurnContext extends Context {
    private GameController gameController;
    private Game game;

    private Callback callback;

    // Context state
    private FamilyMemberColor familyMemberColor;
    private ActionSpace actionSpace;
    private RequiredResourceSet paymentForCard;
    private List<ObtainableResourceSet> chosenCouncilPrivileges;

    public MainTurnContext(UIContextInterface uiContextInterface, GameController gameController, Callback callback) {
        super(uiContextInterface);
        this.gameController = gameController;
        this.game = gameController.getGame();
        this.callback = callback;
        this.addCommand("show-board", this::showBoard, "Show board status");
        this.addCommand("show-player", this::showPlayer, "<username> Show player status");
        this.addCommand("list-players", this::listPlayers, "List players in turn order");
        this.addCommand("spend-servants", this::spendServants, "<number> Commit servants to spend for the next action");
        this.addCommand("place-family-member", this::placeFamilyMember,
                "<black, white, orange, neutral> Go to an action space");
        this.addCommand("play-leader", this::playLeaderCard, "Play a Leader Card from your hand");
        this.addCommand("activate-effect", this::activateLeaderCard, "Activate a Leader’s once per round ability");
        this.addCommand("discard-leader", this::discardLeaderCard,
                "Discard a Leader Card from your hand and immediately receive a Council Privilege");
        this.addCommand("end-turn", this::endTurn, "End your turn");
        uiContextInterface.println("It's your turn!");
    }

    private void showBoard(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");

        // Add 4 rows for the floors
        List<ContainerRow> rows = new ArrayList<>();
        rows.add(new ContainerRow());
        rows.add(new ContainerRow());
        rows.add(new ContainerRow());
        rows.add(new ContainerRow());

        for (int i = 0; i < 4; i++) {
            String description = game.getBoard().getTerritoryTower().getFloors().get(i).toString();
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

            rows.get(i).addColumn(column);
        }
        for (int i = 0; i < 4; i++) {
            String description = game.getBoard().getCharacterTower().getFloors().get(i).toString();
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
            rows.get(i).addColumn(column);
        }
        for (int i = 0; i < 4; i++) {
            String description = game.getBoard().getBuildingTower().getFloors().get(i).toString();
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
            rows.get(i).addColumn(column);
        }
        for (int i = 0; i < 4; i++) {
            String description = game.getBoard().getVentureTower().getFloors().get(i).toString();
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
            rows.get(i).addColumn(column);
        }

        ContainerRow lastRow = new ContainerRow();
        rows.add(lastRow);

        // Production Area
        lastRow.addColumn(getProductionAreaColumn());

        // Harvest Area
        lastRow.addColumn(getHarvestAreaColumn());

        // Market
        lastRow.addColumn(getMarketColumn());

        // Council Palace and dice
        lastRow.addColumn(getCouncilPalaceAndDiceColumn());

        TableLayout layout = new TableLayout(rows);
        uiContextInterface.printLayout(layout);
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

            // TODO once per round effect already activated or not activated yet

            lastRow.addColumn(new TextBox(description));
        }
        for (int i = 0; i < player.getAvailableLeaderCards().size(); i++) {
            String description = player.getAvailableLeaderCards().get(i).toString() + "\n NOT PLAYED YET";
            lastRow.addColumn(new TextBox(description));
        }

        // Personal bonus tile
        ContainerColumn tileColumn = new ContainerColumn();
        lastRow.addColumn(tileColumn);

        String production = "PRODUCTION GIVES YOU: " + player.getBonusTile().getProductionObtainableResourceSet().toString();
        tileColumn.addRow(new TextBox(production));
        String harvest = "HARVEST GIVES YOU: " + player.getBonusTile().getHarvestObtainableResourceSet().toString();
        tileColumn.addRow(new TextBox(harvest));

        // Resources
        String description = "YOUR RESOURCES: " + player.getResources().toString();
        tileColumn.addRow(new TextBox(description));

        uiContextInterface.printLayout(layout);

    }

    private void listPlayers(String[] params) throws InvalidCommandException {
        if (params.length != 0) {
            throw new InvalidCommandException("This command takes no arguments");
        }

        List<Player> players = game.getPlayers();

        for (int i = 1; i <= players.size(); i++) {
            uiContextInterface.println(String.format("%d) %s", i, players.get(i - 1).getUsername()));
        }
    }

    private void spendServants(String[] params) throws InvalidCommandException {
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
            uiContextInterface.println("You cannot do that", true);
        }
    }

    /* -----------------------------------------------------------------
     * Methods for going to an action space
     * The callbacks are a bit messy to follow but this way is quick and
     * convenient for the player
     * ----------------------------------------------------------------- */

    /**
     * "Entry point" for the place-family-member command
     *
     * @param params
     * @throws InvalidCommandException
     * @throws ActionNotAllowedException
     */
    private void placeFamilyMember(String[] params) throws InvalidCommandException, ActionNotAllowedException {
        if (params.length != 1) {
            throw new InvalidCommandException("You must specify what family member you want to use!");
        }

        String familyMember = params[0];

        switch (familyMember) {
            case "black":
                familyMemberColor = FamilyMemberColor.BLACK;
                break;
            case "white":
                familyMemberColor = FamilyMemberColor.WHITE;
                break;
            case "orange":
                familyMemberColor = FamilyMemberColor.ORANGE;
                break;
            case "neutral":
                familyMemberColor = FamilyMemberColor.NEUTRAL;
                break;
            default:
                throw new InvalidCommandException("Invalid family member choice");
        }

        askWhereToPlaceFamilyMember();
    }

    /**
     * Asks the player where he wants to go
     */
    private void askWhereToPlaceFamilyMember() {
        // TODO: filter only action spaces where the player can go
        List<ActionSpace> actionSpaces = gameController.getAllowedActionSpaces();

        SingleChoiceContext<ActionSpace> choiceContext = new SingleChoiceContext<>(uiContextInterface,
                actionSpaces,
                choice -> this.chosenActionSpace(choice));
        choiceContext.setPreviousContext(this);
        uiContextInterface.changeContext(choiceContext);
    }

    /**
     * Takes the action space choice from the player
     * @param actionSpace
     * @throws ActionNotAllowedException
     */
    private void chosenActionSpace(ActionSpace actionSpace) throws ActionNotAllowedException {
        this.actionSpace = actionSpace;

        if(actionSpace instanceof Floor) askWhichResourcesToPayForCard();
        else askWhichCouncilPrivileges();
    }

    /**
     * Asks the player which resources he wants to play for taking the card
     * when occupying a floor
     */
    private void askWhichResourcesToPayForCard() throws ActionNotAllowedException {
        if(!(actionSpace instanceof Floor)) askWhichCouncilPrivileges();

        Floor floor = (Floor) actionSpace;
        DevelopmentCard card = floor.getCard();

        List<RequiredResourceSet> choices = gameController.getAllowedPaymentsForCard(card);
        if(choices.isEmpty()) {
            chosenPaymentForCard(new RequiredResourceSet());
        }
        else if (choices.size() == 1) {
            chosenPaymentForCard(choices.get(0));
        }
        else {
            SingleChoiceContext<RequiredResourceSet> choiceContext;
            choiceContext = new SingleChoiceContext<>(uiContextInterface, choices, this::chosenPaymentForCard);
            choiceContext.setPreviousContext(this);
            uiContextInterface.changeContext(choiceContext);
        }
    }

    /**
     * Takes the choice of the player for which resources to pay for the card
     * @param paymentForCard
     */
    private void chosenPaymentForCard(RequiredResourceSet paymentForCard) throws ActionNotAllowedException {
         this.paymentForCard = paymentForCard;
         askWhichCouncilPrivileges();
    }

    /**
     * Asks the player which council privileges to take as a bonus for
     * occupying an action space
     */
    private void askWhichCouncilPrivileges() throws ActionNotAllowedException {
        ObtainableResourceSet bonus = actionSpace.getBonus();
        int bonusCouncilPrivileges = bonus.getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);

        if (bonusCouncilPrivileges == 0) {
            chosenCouncilPrivileges(new ArrayList<>());
        }
        else if (bonusCouncilPrivileges == 1) {
            List<ObtainableResourceSet> allowedCouncilPrivileges = game.getAllowedCouncilPrivileges();
            SingleChoiceContext<ObtainableResourceSet> choiceContext;
            choiceContext = new SingleChoiceContext<>(uiContextInterface,
                    allowedCouncilPrivileges,
                    choice -> {
                        List<ObtainableResourceSet> privileges = new ArrayList<>();
                        privileges.add(choice);
                        chosenCouncilPrivileges(privileges);
                    });
            choiceContext.setPreviousContext(this);
            uiContextInterface.changeContext(choiceContext);
        }
        else {
            List<ObtainableResourceSet> allowedCouncilPrivileges = game.getAllowedCouncilPrivileges();
            MultipleChoiceContext<ObtainableResourceSet> choiceContext;
            choiceContext = new MultipleChoiceContext<>(uiContextInterface,
                    allowedCouncilPrivileges,
                    bonusCouncilPrivileges,
                    bonusCouncilPrivileges,
                    choices -> chosenCouncilPrivileges(choices),
                    true);
            choiceContext.setPreviousContext(this);
            uiContextInterface.changeContext(choiceContext);
        }
    }

    /**
     * Takes the player choice for which council privileges to take
     * @param chosenCouncilPrivileges
     */
    private void chosenCouncilPrivileges(List<ObtainableResourceSet> chosenCouncilPrivileges) throws ActionNotAllowedException {
        this.chosenCouncilPrivileges = chosenCouncilPrivileges;

        if(actionSpace instanceof Floor) callback.goToFloor((Floor)actionSpace, familyMemberColor, chosenCouncilPrivileges, paymentForCard);
        else callback.goToActionSpace(actionSpace, familyMemberColor, chosenCouncilPrivileges);
    }

    private void discardLeaderCard(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");

        List<LeaderCard> availableLeaderCards = gameController.getAllowedLeaderCards();
        SingleChoiceContext<LeaderCard> choiceContext = new SingleChoiceContext<>(uiContextInterface,
                availableLeaderCards,
                chosenLeaderCard -> chooseCouncilPrivilegeForDiscardingLeaderCard(chosenLeaderCard)
        );
        choiceContext.setPreviousContext(this);
        uiContextInterface.changeContext(choiceContext);
    }

    private void chooseCouncilPrivilegeForDiscardingLeaderCard(LeaderCard card) {
        List<ObtainableResourceSet> allowedCouncilPrivileges = game.getAllowedCouncilPrivileges();
        SingleChoiceContext<ObtainableResourceSet> choiceContext;
        choiceContext = new SingleChoiceContext<>(uiContextInterface,
                allowedCouncilPrivileges,
                choice -> callback.discardLeaderCard(card, choice)
        );
        choiceContext.setPreviousContext(this);
        uiContextInterface.changeContext(choiceContext);
    }

    private void playLeaderCard(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");

        List<LeaderCard> availableLeaderCards = gameController.getAllowedLeaderCards();
        SingleChoiceContext<LeaderCard> choiceContext = new SingleChoiceContext<>(uiContextInterface,
                availableLeaderCards,
                choice -> callback.playLeaderCard(choice));
        choiceContext.setPreviousContext(this);
        uiContextInterface.changeContext(choiceContext);
    }

    private void activateLeaderCard(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");

        List<Card> activatableCards = gameController.getActivatableCards();

        Map<OncePerRoundEffectInterface, Card> effectsToCards = new HashMap<>();

        List choices = new ArrayList<>();

        for (Card card : activatableCards) {
            List<OncePerRoundEffectInterface> effects = card.getEffectsContainer().getEffectsImplementing(OncePerRoundEffectInterface.class);
            for (OncePerRoundEffectInterface e : effects) {
                effectsToCards.put(e, card);
                String description = card.getName() + ": " + e.toString();
                ChoosableItem<OncePerRoundEffectInterface> choice = new ChoosableItem(description, e);
                choices.add(choice);
            }
        }

        SingleChoiceContext choiceContext;
        choiceContext = new SingleChoiceContext<OncePerRoundEffectInterface>(uiContextInterface,
                choices,
                choice -> {
                    Card chosenCard = effectsToCards.get(choice);
                    callback.activateOncePerRoundEffect(chosenCard, choice);
                });
        choiceContext.setPreviousContext(this);
        uiContextInterface.changeContext(choiceContext);

    }

    private void endTurn(String[] params) throws InvalidCommandException, ActionNotAllowedException {
        callback.endTurn();
    }

    public interface Callback {

        void spendServants(int servants) throws ActionNotAllowedException;

        void goToFloor(Floor floor, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges, RequiredResourceSet paymentForCard) throws ActionNotAllowedException;

        void goToActionSpace(ActionSpace actionSpace, FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> councilPrivileges) throws ActionNotAllowedException;

        void discardLeaderCard(LeaderCard leaderCard, ObtainableResourceSet privilege) throws ActionNotAllowedException;

        void playLeaderCard(LeaderCard leaderCard) throws ActionNotAllowedException;

        void endTurn() throws ActionNotAllowedException;

        void activateOncePerRoundEffect(Card Card, OncePerRoundEffectInterface effect) throws ActionNotAllowedException;

    }

    /*
     * Methods that render columns/rows/etc
     */
    private ColumnInterface getMarketColumn() {
        ContainerColumn marketColumn = new ContainerColumn();
        marketColumn.setTitle("MARKET");

        String market1 = String.format("M1: %s", game.getBoard().getMarket1());
        marketColumn.addRow(new TextBox(market1));

        String market2 = String.format("M2: %s", game.getBoard().getMarket2());
        marketColumn.addRow(new TextBox(market2));

        if (game.getPlayers().size() > 2) {
            String market3 = String.format("M3: %s", game.getBoard().getMarket3());
            marketColumn.addRow(new TextBox(market3));
        }
        if (game.getPlayers().size() > 3) {
            String market4 = String.format("M4: %s", game.getBoard().getMarket4());
            marketColumn.addRow(new TextBox(market4));
        }
        return marketColumn;
    }

    private ColumnInterface getProductionAreaColumn() {
        ContainerColumn productionAreaColumn = new ContainerColumn();
        productionAreaColumn.setTitle("PRODUCTION");

        String mainProductionArea = "Main production area:\n"
                + game.getBoard().getSmallProductionArea().getOccupantsString();
        productionAreaColumn.addRow(new TextBox(mainProductionArea));

        if (game.getPlayers().size() > 2) {
            String secondaryProductionArea = "Secondary production area:\n"
                    + game.getBoard().getBigProductionArea().getOccupantsString();
            productionAreaColumn.addRow(new TextBox(secondaryProductionArea));
        }

        return productionAreaColumn;
    }

    private ColumnInterface getHarvestAreaColumn() {
        ContainerColumn harvestAreaColumn = new ContainerColumn();
        harvestAreaColumn.setTitle("HARVEST");

        String mainHarvestArea = "Main harvest area:\n"
                + game.getBoard().getSmallHarvestArea().getOccupantsString();
        harvestAreaColumn.addRow(new TextBox(mainHarvestArea));

        if (game.getPlayers().size() > 2) {
            String secondaryHarvestArea = "Secondary harvest area:\n"
                    + game.getBoard().getBigHarvestArea().getOccupantsString();
            harvestAreaColumn.addRow(new TextBox(secondaryHarvestArea));
        }

        return harvestAreaColumn;
    }

    private ColumnInterface getCouncilPalaceAndDiceColumn() {
        // Council palace
        ContainerColumn councilPalaceColumn = new ContainerColumn();
        councilPalaceColumn.setTitle("COUNCIL PALACE");

        String councilPalaceArea = "Gives: " + game.getBoard().getCouncilPalace().getBonus() + "\n"
                + game.getBoard().getCouncilPalace().getOccupantsString();
        councilPalaceColumn.addRow(new TextBox(councilPalaceArea));

        // Dice values
        StringBuilder dice = new StringBuilder();
        dice.append("Black Die: ").append(game.getBlackDie()).append("\n")
            .append("White Die: ").append(game.getWhiteDie()).append("\n")
            .append("Orange Die: ").append(game.getOrangeDie());
        councilPalaceColumn.addRow(new TextBox(dice.toString()));

        return councilPalaceColumn;
    }
}
