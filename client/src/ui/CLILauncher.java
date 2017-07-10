package ui;

import client.ClientController;

import java.io.IOException;

/**
 * This class is responsible for starting the command line user interface (CLI UI)
 */
public class CLILauncher {
    /**
     * This class is not designed to be instantiated
     */
    private CLILauncher() {
    }

    public static void main(String[] args) throws IOException {
        new ClientController(UIType.CLI);
    }
}
