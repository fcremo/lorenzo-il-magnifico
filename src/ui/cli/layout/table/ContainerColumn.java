package ui.cli.layout.table;

import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * True if the column should draw borders between its rows
     */
    private boolean drawBorders = true;

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
        int remainingHeight = height;
        if(height == 0) {
            remainingHeight = getHeightRequirement(width);
        }

        if(drawBorders && height != 0) {
            remainingHeight = remainingHeight - 1 - rows.size();
        }

        // Get the sum of the weights of the rows
        int weightsSum = rows.stream()
                             .mapToInt(RowInterface::getWeight)
                             .sum();

        StringJoiner sj = new StringJoiner("\n");

        String horizontalBorder = String.join("", Collections.nCopies(width, "-"));
        if(drawBorders) sj.add(horizontalBorder);

        // Render each row proportionally
        for(RowInterface row : rows) {
            int rowHeight = row.getWeight() * remainingHeight / weightsSum;
            String renderedRow = row.render(width, rowHeight);
            sj.add(renderedRow);
            if(drawBorders) sj.add(horizontalBorder);
        }

        return sj.toString();
    }

    @Override
    public int getHeightRequirement(int width) {
        // The height requirement of a column is the sum of the height requirement of its rows
        int heightRequirement = 0;
        for(RowInterface row : rows){
            heightRequirement += row.getHeightRequirement(width);
        }
        if(drawBorders) {
            heightRequirement += rows.size() + 1;
        }
        return heightRequirement;
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

    public boolean getDrawBorders() {
        return drawBorders;
    }

    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }
}
