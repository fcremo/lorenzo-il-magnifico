package ui.cli.contexts;

public class WaitingContext extends Context {
    public WaitingContext(UIContextInterface uiContextInterface, String message) {
        super(uiContextInterface);
        this.uiContextInterface.println(message);
        this.addCommand("exit", params -> System.exit(0), "Exit the game");
    }
}
