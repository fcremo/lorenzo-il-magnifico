package ui.cli.contexts;

public class WaitingContext extends Context {
    public WaitingContext(String message) {
        System.out.println(message);
        this.addCommand("exit", params -> System.exit(0), "Exit the game");
    }
}
