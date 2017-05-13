package ui.cli;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This class is responsible for starting the command line user interface (CLI UI)
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger("CLI Main");

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting Lorenzo Il Magnifico CLI");

        System.out.println("Welcome to Lorenzo Il Magnifico CLI!");

        // Start the UI
        CLIUserInterface UI = new CLIUserInterface();
        UI.start();
    }
}
