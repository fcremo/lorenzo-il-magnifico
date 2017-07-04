package ui.cli.contexts;

public class WaitingContext extends Context {
    public WaitingContext(PrintInterface printInterface, String message) {
        super(printInterface);
        printer.println(message);
        this.addCommand("exit", params -> System.exit(0), "Exit the game");
    }
}
