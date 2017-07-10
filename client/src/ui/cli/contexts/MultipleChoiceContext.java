package ui.cli.contexts;

import gamecontroller.exceptions.ActionNotAllowedException;
import ui.cli.exceptions.InvalidChoiceException;
import ui.cli.exceptions.InvalidCommandException;

import java.util.*;

public class MultipleChoiceContext<T> extends Context {
    private List<T> choices = new ArrayList<>();
    private List<T> allowedChoices;
    private int minChoices = 1;
    private int maxChoices;
    private boolean choicesMustBeUnique = true;

    private Callback callback;

    public MultipleChoiceContext(UIContextInterface uiContextInterface, List<T> allowedChoices, int minChoices, int maxChoices, Callback callback, boolean choicesMustBeUnique) {
        super(uiContextInterface);
        this.allowedChoices = new ArrayList<>(allowedChoices);
        this.minChoices = minChoices;
        this.maxChoices = maxChoices;
        this.choicesMustBeUnique = choicesMustBeUnique;
        this.callback = callback;
        this.addCommand("show", this::showAll, "Show current choices and available ones");
        this.addCommand("show-chosen", this::showChosen, "Show current choices");
        this.addCommand("show-available", this::showAllowed, "Show available choices");
        this.addCommand("choose", this::choose, "{index [...]} Add item(s) to the chosen ones");
        this.addCommand("remove", this::remove, "{index} Remove item from the chosen ones");
        this.addCommand("done", this::done, "Confirm choice");

        this.printCurrentAndAvailableChoices();
    }

    private void showAll(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");
        printCurrentAndAvailableChoices();
        uiContextInterface.printPrompt();
    }

    private void showChosen(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");
        printCurrentChoices();
        uiContextInterface.printPrompt();
    }

    private void showAllowed(String[] params) throws InvalidCommandException {
        if (params.length != 0) throw new InvalidCommandException("This command takes no arguments");
        printAvailableChoices();
        uiContextInterface.printPrompt();

    }

    private void printCurrentAndAvailableChoices() {
        if(!choices.isEmpty()) {
            uiContextInterface.println("So far you have chosen:");
            for (int i = 1; i <= choices.size(); i++) {
                uiContextInterface.println(i + ") " + choices.get(i - 1).toString());
            }
        }
        uiContextInterface.println("");
        printAvailableChoices();
    }

    private void printCurrentChoices() {
        if(!choices.isEmpty()) {
            uiContextInterface.println("So far you have chosen:");
            for (int i = 1; i <= choices.size(); i++) {
                uiContextInterface.println(i + ") " + choices.get(i - 1).toString());
            }
        }
        else {
            uiContextInterface.println("So far you haven't chosen anything");
        }
    }

    private void printAvailableChoices() {
        if(minChoices == 0){
            if(getRemainingChoices() == 1) uiContextInterface.println("You can choose one of:");
            else uiContextInterface.println(String.format("You can choose up to %d between:", getRemainingChoices()));
        }
        else if (minChoices == 1) {
            if(getRemainingChoices() == 1) uiContextInterface.println("Choose one:");
            else uiContextInterface.println(String.format("You must choose up to %d between:", getRemainingChoices()));
        }
        else {
            uiContextInterface.println(String.format("You can choose between %d and %d between (%d remaining):", minChoices, maxChoices, getRemainingChoices()));
        }

        for (int i = 1; i <= allowedChoices.size(); i++) {
                uiContextInterface.println(i + ") " + allowedChoices.get(i - 1).toString());
        }

        if(choicesMustBeUnique) uiContextInterface.println("The choices must be unique");
    }

    private void choose(String[] params) throws InvalidCommandException {
        if (params.length == 0) throw new InvalidCommandException("You have to specify what you want to choose!");

        List<Integer> chosenIndexes = new ArrayList<>();

        for(String s : params){
            try {
                int chosenIndex = Integer.parseInt(s);
                if (chosenIndex < 1 || chosenIndex > allowedChoices.size()) {
                    throw new InvalidCommandException(String.format("Choose between %d and %d", 1, allowedChoices.size()));
                }
                chosenIndexes.add(chosenIndex);
            }
            catch (NumberFormatException e) {
                throw new InvalidCommandException(String.format("%s is not a valid index", s));
            }
        }

        if(chosenIndexes.size() > getRemainingChoices()) {
            throw new InvalidCommandException(String.format(
                    "You can choose up to %d, you've chosen %d",
                    maxChoices,
                    chosenIndexes.size()));
        }

        if(!choicesMustBeUnique) {
            chosenIndexes.forEach(i -> choices.add(allowedChoices.get(i - 1)));
        }

        else {
            // Check for duplicate choices
            if(!findDuplicates(chosenIndexes).isEmpty()) throw new InvalidCommandException("Your choice must be unique");

            // Sort choices in reverse so that removing them
            // one by one from the allowed choices does not change the index of the next ones
            chosenIndexes.sort(Comparator.reverseOrder());

            chosenIndexes.forEach(i -> {
                choices.add(allowedChoices.get(i - 1));
                allowedChoices.remove(i - 1);
            });
        }
    }

    private void remove(String[] params) throws InvalidCommandException {
        if (params.length != 1) throw new InvalidCommandException("You have to specify what you want to remove!");

        int chosenIndex;

        try {
            chosenIndex = Integer.parseInt(params[0]);
            if (chosenIndex < 1 || chosenIndex > choices.size()) {
                throw new InvalidCommandException(String.format("Choose between %d and %d", 1, choices.size()));
            }
        }
        catch (NumberFormatException e) {
            throw new InvalidCommandException(String.format("%s is not a valid index", params[0]));
        }

        if(!allowedChoices.contains(choices.get(chosenIndex - 1))){
            allowedChoices.add(choices.get(chosenIndex - 1));
        }
        choices.remove(chosenIndex - 1);
    }

    private void done(String[] params) throws InvalidCommandException, ActionNotAllowedException {
        if (params.length != 0) throw new InvalidCommandException("This command does not take arguments!");

        if(choices.size() < minChoices) throw new ActionNotAllowedException(String.format("You have to choose at least %d objects", minChoices));

        try {
            callback.choose(choices);
        }
        catch (InvalidChoiceException e) {
            throw new InvalidCommandException("Your choice is invalid");
        }
    }

    private int getRemainingChoices() {
        return maxChoices - choices.size();
    }

    /**
     * From https://stackoverflow.com/a/16467773
     * @param list
     * @param <T>
     * @return
     */
    private <T> Set<T> findDuplicates(Collection<T> list) {
        Set<T> duplicates = new LinkedHashSet<>();
        Set<T> uniques = new HashSet<>();

        for(T t : list) {
            if(!uniques.add(t)) {
                duplicates.add(t);
            }
        }

        return duplicates;
    }

    @FunctionalInterface
    public interface Callback<T> {
        void choose(List<T> choices) throws ActionNotAllowedException, InvalidChoiceException;
    }
}
