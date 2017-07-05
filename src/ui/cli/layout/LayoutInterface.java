package ui.cli.layout;

public interface LayoutInterface {
    /**
     * Render the layout to the given width and height
     * @param width the target width
     * @param height the target height. Ignored if 0.
     * @return
     */
    String render(int width, int height);
}
