package ui.cli.contexts;

public class WaitingForGameToStartContext extends Context {
    public WaitingForGameToStartContext() {
        System.out.println("Waiting for the game to start...");
        this.addCommand("exit", params -> System.exit(0), "Exit the game");
    }
}
