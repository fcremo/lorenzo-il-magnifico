package ui.cli.layout.table;

import ui.cli.layout.LayoutInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * This class implements a table layout
 *
 * A table is made of one or more rows
 */
public class TableLayout implements LayoutInterface {
    List<RowInterface> rows = new ArrayList<>();

    public TableLayout() {
    }

    public TableLayout(List<RowInterface> rows) {
        this.rows = rows;
    }

    @Override
    public String render(int width, int height) {
        // Get the sum of the weights of the rows
        int weightsSum = rows.stream()
                             .mapToInt(RowInterface::getWeight)
                             .sum();

        StringJoiner sj = new StringJoiner("\n");

        // Render each row with a height proportional to its weight
        rows.stream().map(row -> row.render(width, row.getWeight() * height / weightsSum))
                     .forEach(sj::add);

        return sj.toString();
    }

    public List<RowInterface> getRows() {
        return rows;
    }

    public void setRows(List<RowInterface> rows) {
        this.rows = rows;
    }

    public void addRow(RowInterface row) {
        this.rows.add(row);
    }
}
