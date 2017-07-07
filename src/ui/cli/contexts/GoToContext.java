package ui.cli.contexts;

import client.exceptions.NetworkException;
import model.Game;
import model.board.actionspace.ActionSpace;
import model.board.actionspace.Floor;
import model.player.FamilyMemberColor;
import model.resource.ObtainableResource;
import model.resource.ObtainableResourceSet;
import model.resource.RequiredResourceSet;
import server.exceptions.ActionNotAllowedException;
import ui.cli.exceptions.InvalidCommandException;

import java.rmi.RemoteException;
import java.util.List;

public class GoToContext extends Context {
    private Game game;

    private Callback callback;

    private FamilyMemberColor familyMemberColor;

    private String where;

    public GoToContext(UIContextInterface uiContextInterface, Game game, Callback callback, Context previousContext, String where) throws InvalidCommandException {
        super(uiContextInterface, previousContext);
        this.game = game;
        this.callback = callback;
        this.where = where;

        switch (where) {
            case "council":
                int allowedCouncilPrivileges = game.getBoard().getCouncilPalace().getBonus().getObtainedAmount(ObtainableResource.COUNCIL_PRIVILEGES);
                List<ObtainableResourceSet> allowedChoices = game.getCouncilPrivileges();
                Context chooseCouncilPrivilegeContext = new MultipleChoiceContext<>(uiContextInterface,
                        allowedChoices,
                        allowedCouncilPrivileges,
                        allowedCouncilPrivileges,
                        this::chooseCouncilFavors,
                        true);
                uiContextInterface.changeContext(chooseCouncilPrivilegeContext);
                break;
            default:
                throw new InvalidCommandException("Invalid action space");
        }


        this.addCommand("choose-family-member", this::chooseFamilyMember,
                "{black, white, orange, neutral} Choose this family member to use for going to the action space");
        this.addCommand("choose-family-member", this::chooseFamilyMember,
                "{black, white, orange, neutral} Choose this family member to use for going to the action space");

        if(where.equals("floor")) {
            this.addCommand("choose-floor", this::chooseFamilyMember,
                    "{black, white, orange, neutral} Choose this family member to use for going to the action space");
        }
        else if(where.equals("market")) {
            this.addCommand("choose-market", this::chooseFamilyMember,
                    "{black, white, orange, neutral} Choose this family member to use for going to the action space");
        }



        uiContextInterface.println("It's your turn!");
    }

    private void chooseCouncilFavors(List<ObtainableResourceSet> favors) throws InvalidCommandException, NetworkException, RemoteException {
        try {
            callback.goToCouncilPalace(familyMemberColor, favors);
        }
        catch (ActionNotAllowedException e) {
            goBack(new String[0]);
        }
    }

    public interface Callback {
        void goToCouncilPalace(FamilyMemberColor familyMemberColor, List<ObtainableResourceSet> chosenPrivileges) throws NetworkException, RemoteException, ActionNotAllowedException;
    }

}
