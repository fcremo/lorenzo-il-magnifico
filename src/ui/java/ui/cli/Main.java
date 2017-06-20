package ui.cli;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This class is responsible for starting the command line user interface (CLI UI)
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger("CLI Main");

    /**
     * This class is not designed to be instantiated
     */
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting Lorenzo Il Magnifico CLI");

        OptionParser parser = new OptionParser();
        parser.accepts("autologin").withRequiredArg();

        System.out.println("Welcome to Lorenzo Il Magnifico CLI!");

        // Start the UI
        CLIUserInterface UI = new CLIUserInterface();
        OptionSet options = parser.parse(args);
        if (options.has("autologin")) {
            UI.start((String)options.valueOf("autologin"));
        }
        else {
            UI.start();
        }
    }
}
