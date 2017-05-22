package ui.cli.contexts;

import ui.cli.InvalidCommandException;

/**
 * This is the interface for a generic CLI UI command
 */
@FunctionalInterface
public interface Command {
    /**
     * Execute the command with the provided arguments
     *
     * @param arguments
     * @throws InvalidCommandException
     */
    void execute(String[] arguments) throws InvalidCommandException;
}
