package ui.cli.layout.table;

import ui.cli.layout.LayoutInterface;

public interface RowInterface extends LayoutInterface {
    @Override
    String render(int width, int height);

    int getWeight();

    int getHeightRequirement(int width);
}
