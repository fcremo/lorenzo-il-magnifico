package ui.cli.layout.table;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a row which holds columns
 *
 * A container row is made of at least one column
 */
public class ContainerRow implements RowInterface {
    private List<ColumnInterface> columns = new ArrayList<>();

    private int weight = 1;

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
        int weightsSum = columns.stream()
                                .mapToInt(ColumnInterface::getWeight)
                                .sum();

        // Render each column proportionally and split them by lines
        List<List<String>> rendered;
        rendered = columns.stream()
               // Render columns
               .map(col -> col.render(width * col.getWeight() / weightsSum, height))
               // Split rendered columns by lines
               .map(str -> Arrays.asList(str.split("\n")))
               .collect(Collectors.toList());

        // We have an array of [columns][rows] but we want [rows][columns]
        // need to transpose rows and columns
        rendered = transpose(rendered);

        StringJoiner sj = new StringJoiner("\n");

        rendered.stream()
                .map( row -> row.stream().reduce((string, append) -> string + append).get())
                .forEach(sj::add);


        // Create an empty row of the right width
        String emptyRow = new String(new char[width]).replace("\0", " ");

        int numRows = sj.toString().split("\n").length;
        // Fill the row with empty text rows
        for(int row=numRows; row<height; row++) {
            sj.add(emptyRow);
        }

        return sj.toString();
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

    /**
     * Transpose a bidimensional list
     * @param l
     * @return
     */
    private static List<List<String>> transpose(List<List<String>> l){
        // Find the number of rows
        int numRows = l.stream().mapToInt(List::size).max().getAsInt();

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
