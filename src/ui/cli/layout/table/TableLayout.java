package ui.cli.layout.table;

import ui.cli.layout.LayoutInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * This class implements a table layout
 *
 * A table is made of one or more rows
 */
public class TableLayout implements LayoutInterface {
    private List<RowInterface> rows = new ArrayList<>();

    /**
     * True if the table should have borders
     */
    private boolean drawBorders = true;

    /**
     * True if the height requirement must be honored
     */
    private boolean strictHeight = false;

    public TableLayout() {
    }

    public TableLayout(List<? extends RowInterface> rows) {
        this.rows = (List<RowInterface>) rows;
    }

    @Override
    public String render(int width, int height) {
        int availableHeight = height;
        if(drawBorders){
            // Leave space for the borders
            availableHeight = availableHeight - 1 - rows.size();
        }

        /*
          * We need to get the height requirement of each row to see if we can satisfy the global height requirement.
          * There are 2 possibilities:
          * 1) the sum of the row heights is less than the global requirement, or strictHeight is false:
          *     In this case we render the row without cutting them
          * 2) the sum of the row heights is more than the global requirement and strictHeight is true:
          *     In this case each row gets to occupy a height proportional to its weight
          */

        if(!strictHeight
            || rows.stream()
                   .mapToInt(row -> row.getHeightRequirement(width))
                   .sum() <= availableHeight ) {
            return renderFullHeight(width);
        }
        else {
            return renderProportionally(width, height);
        }
    }

    /**
     * Render each row of the table with a height proportional to its weight
     * @param width the target width
     * @param height the target height
     * @return
     */
    private String renderProportionally(int width, int height) {
        int availableHeight = height;
        if(drawBorders){
            // Leave space for the borders
            availableHeight = availableHeight - 1 - rows.size();
        }

        // Get the sum of the weights of the rows
        int weightsSum = rows.stream()
                             .mapToInt(RowInterface::getWeight)
                             .sum();

        String horizontalBorder = String.join("", Collections.nCopies(width, "-"));

        StringJoiner sj = new StringJoiner("\n");

        if(drawBorders) sj.add(horizontalBorder);

        // Render each row with a height proportional to its weight
        for(RowInterface row : rows){
            int rowHeight = row.getWeight() * availableHeight / weightsSum;
            sj.add(row.render(width, rowHeight));
            if(drawBorders) sj.add(horizontalBorder);
        }


        return sj.toString();
    }

    /**
     * Render each row of the table without height requirements
     * @param width
     * @return
     */
    private String renderFullHeight(int width) {
        String horizontalBorder = String.join("", Collections.nCopies(width, "-"));

        StringJoiner sj = new StringJoiner("\n");

        if(drawBorders) sj.add(horizontalBorder);

        // Render each row
        for(RowInterface row : rows){
            sj.add(row.render(width, 0));
            if(drawBorders) sj.add(horizontalBorder);
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

    public boolean getDrawBorders() {
        return drawBorders;
    }

    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }

    public boolean getStrictHeight() {
        return strictHeight;
    }

    public void setStrictHeight(boolean strictHeight) {
        this.strictHeight = strictHeight;
    }
}
