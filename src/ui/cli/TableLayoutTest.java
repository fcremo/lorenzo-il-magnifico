package ui.cli;

import ui.cli.layout.table.ContainerColumn;
import ui.cli.layout.table.ContainerRow;
import ui.cli.layout.table.TableLayout;
import ui.cli.layout.table.TextBox;

public class TableLayoutTest {
    public static void main(String[] args) {
        TableLayout layout = new TableLayout();

        ContainerRow r1 = new ContainerRow();

        r1.addColumn(new TextBox("T0: Commercial Hub\n requirements: null\n effects: null"));
        r1.addColumn(new TextBox("T0: Commercial Hub\n requirements: [3 GOLD]\n effects: null"));
        r1.addColumn(new TextBox("B0: Carpenter's Shop\nRequires: 1 GOLD, 2 WOOD\nEffects: null\nProduction (at 4):1 WOOD => 3 GOLD or 2 WOOD => 5 GOLD"));
        r1.addColumn(new TextBox("T0: Commercial Hub\n requirements: [3 GOLD]\n effects: null"));
        layout.addRow(r1);

        ContainerRow lastRow = new ContainerRow();

        ContainerColumn productionAreaColumn = new ContainerColumn();
        productionAreaColumn.setDrawBorders(false);
        productionAreaColumn.addRow(new TextBox("Main production area:\nIs not occupied yet"));
        lastRow.addColumn(productionAreaColumn);

        ContainerColumn harvestAreaColumn = new ContainerColumn();
        harvestAreaColumn.setDrawBorders(false);
        harvestAreaColumn.addRow(new TextBox("Main harvest area:\nIs not occupied yet"));
        lastRow.addColumn(harvestAreaColumn);

        ContainerColumn councilPalaceColumn = new ContainerColumn();
        councilPalaceColumn.setDrawBorders(false);
        councilPalaceColumn.addRow(new TextBox("Council palace:\nGives: 1 GOLD, 1 COUNCIL_PRIVILEGES\nIs not occupied yet"));
        lastRow.addColumn(councilPalaceColumn);

        layout.addRow(lastRow);

        System.out.println(layout.render(160, 20));
    }
}
