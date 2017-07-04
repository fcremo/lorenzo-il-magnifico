package ui.cli.layout.table;

import ui.cli.layout.LayoutInterface;

public interface ColumnInterface extends LayoutInterface {
    String render(int width, int height);

    int getWeight();
}
