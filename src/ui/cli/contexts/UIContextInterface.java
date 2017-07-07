package ui.cli.contexts;

import ui.cli.layout.LayoutInterface;

public interface UIContextInterface {
    /**
     * Prints a line on the screen, and reprints the prompt
     * @param line the line to print
     */
    void println(String line);

    /**
     * Prints a line on the screen
     *
     * Pass false to reprintPrompt to display multiple lines
     *
     * @param line the line to print
     * @param reprintPrompt if true the prompt is reprinted
     */
    void println(String line, boolean reprintPrompt);

    /**
     * Prints a layout
     * @param layout
     */
    void printLayout(LayoutInterface layout);

    /**
     * Prints the prompt
     */
    void printPrompt();

    /**
     * Changes the current context
     * @param context
     */
    void changeContext(Context context);
}
