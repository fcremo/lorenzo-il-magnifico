package ui.cli.layout.table;

import ui.cli.layout.LayoutInterface;

public interface ColumnInterface extends LayoutInterface {
    @Override
    String render(int width, int height);

    int getWeight();

    int getHeightRequirement(int width);
}
