package ui.cli.layout.table;

import java.util.*;

/**
 * This class represents a row which holds columns
 *
 * A container row is made of at least one column
 */
public class ContainerRow implements RowInterface {
    private List<ColumnInterface> columns = new ArrayList<>();

    private int weight = 1;

    /**
     * True if the row should draw borders between its columns
     */
    private boolean drawBorders = true;

    public ContainerRow() {
        // Empty constructor
    }

    public ContainerRow(List<ColumnInterface> columns) {
        this.columns = columns;
    }

    public ContainerRow(List<ColumnInterface> columns, int weight) {
        this.columns = columns;
        this.weight = weight;
    }

    @Override
    public String render(int width, int height) {
        // Take into account the width occupied by the borders
        int totalAvailableWidth = width;
        if(drawBorders) totalAvailableWidth = totalAvailableWidth - 1 - columns.size();

        int remainingWidth = totalAvailableWidth;

        // If height == 0 the row should be rendered to full height without constraints
        if(height == 0) height = getHeightRequirement(width);

        int weightsSum = columns.stream()
                                .mapToInt(ColumnInterface::getWeight)
                                .sum();

        // Render columns proportionally and split them by lines
        List<List<String>> rendered = new ArrayList<>();
        for(ColumnInterface column : columns.subList(0, columns.size() - 1)){
            int columnWidth = totalAvailableWidth * column.getWeight() / weightsSum;
            String renderedColumn = column.render(columnWidth, height);
            rendered.add(Arrays.asList(renderedColumn.split("\n")));
            remainingWidth -= columnWidth;
        }
        // Render last column to fill all remaining space
        String renderedColumn = columns.get(columns.size() - 1).render(remainingWidth, height);
        rendered.add(Arrays.asList(renderedColumn.split("\n")));


        // We have an array of [columns][rows] but we want [rows][columns]
        // transpose rows and columns
        rendered = transpose(rendered);

        StringJoiner resultRender = new StringJoiner("\n");

        for(List<String> row : rendered) {
            StringBuilder rowStringBuilder = new StringBuilder();
            if(drawBorders) rowStringBuilder.append("|");
            for(String column : row) {
                rowStringBuilder.append(column);
                if(drawBorders) rowStringBuilder.append("|");
            }
            resultRender.add(rowStringBuilder.toString());
        }

        return resultRender.toString();
    }

    @Override
    public int getHeightRequirement(int width) {
        // Take into account the width occupied by the borders
        // Need to assign remainingWidth only one time to use it in a lambda (?)
        int remainingWidth = width;
        if(drawBorders) remainingWidth = width - 1 - columns.size();


        int weightsSum = columns.stream()
                                .mapToInt(ColumnInterface::getWeight)
                                .sum();

        // The height requirement of a row is the maximum height requirement of its columns
        int maxHeight = 0;
        for(ColumnInterface column : columns){
            int columnWidth = remainingWidth * column.getWeight() / weightsSum;
            int colHeight = column.getHeightRequirement(columnWidth);

            if(maxHeight < colHeight) maxHeight = colHeight;
        }
        return maxHeight;
    }

    public List<ColumnInterface> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInterface> columns) {
        this.columns = columns;
    }

    public void addColumn(ColumnInterface column) {
        this.columns.add(column);
    }

    public void removeColumn(ColumnInterface column) {
        this.columns.remove(column);
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

    /**
     * Transpose a bidimensional list
     * @param l
     * @return
     */
    private static List<List<String>> transpose(List<List<String>> l){
        // Find the number of rows
        int numRows = l.stream().mapToInt(List::size).max().orElse(0);

        List<List<String>> tmp = new ArrayList<>();

        for(int i=0; i<numRows; i++) {
            List<String> row = new ArrayList<>();

            // Append the relevant columns to the row
            for(List<String> cols : l){
                if(cols.size() > i){
                    row.add(cols.get(i));
                }
            }

            tmp.add(row);
        }

        return tmp;
    }
}
