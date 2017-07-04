package ui.cli.layout.table;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * This class represents a container column in a table layout
 *
 * A container column is made by one or more rows
 */
public class ContainerColumn implements ColumnInterface {
    private List<RowInterface> rows = new ArrayList<>();

    private int weight;

    public ContainerColumn() {
        this.weight = 1;
    }

    public ContainerColumn(List<RowInterface> rows) {
        this.rows = rows;
        this.weight = 1;
    }

    public ContainerColumn(List<RowInterface> rows, int weight) {
        this.rows = rows;
        this.weight = weight;
    }

    @Override
    public String render(int width, int height) {
        // Get the sum of the weights of the rows
        int weightsSum = rows.stream()
                             .mapToInt(RowInterface::getWeight)
                             .sum();

        StringJoiner sj = new StringJoiner("\n");

        // Render each row proportionally
        rows.stream()
            .map(row -> row.render(width, row.getWeight() * height / weightsSum))
            .forEach(sj::add);

        int numRows = sj.toString().split("\n").length;

        // Create an empty row of the right width
        String emptyRow = new String(new char[width]).replace("\0", " ");
        // Fill the column with empty text rows
        for(int row=numRows; row<height; row++) {
            sj.add(emptyRow);
        }

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

    public void removeRow(RowInterface row) {
        this.rows.remove(row);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
