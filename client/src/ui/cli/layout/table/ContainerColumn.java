package ui.cli.layout.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * This class represents a container column in a table layout
 * <p>
 * A container column is made by one or more rows
 */
public class ContainerColumn implements ColumnInterface {
    private List<RowInterface> rows = new ArrayList<>();

    private int weight;

    private String title;

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
        if (height == 0) {
            remainingHeight = getHeightRequirement(width);
        }

        if (drawBorders && height != 0) {
            remainingHeight = remainingHeight - 1 - rows.size();
        }

        if (title != null && height != 0) {
            remainingHeight = remainingHeight - 1;
        }

        // Get the sum of the weights of the rows
        int weightsSum = rows.stream()
                             .mapToInt(RowInterface::getWeight)
                             .sum();

        StringJoiner sj = new StringJoiner("\n");

        String horizontalBorder = String.join("", Collections.nCopies(width, "-"));
        if (drawBorders) sj.add(horizontalBorder);

        if (title != null) sj.add(renderTitle(width));
        // Render each row proportionally
        for (RowInterface row : rows) {
            int rowHeight = row.getWeight() * remainingHeight / weightsSum;
            String renderedRow = row.render(width, rowHeight);
            sj.add(renderedRow);
            if (drawBorders) sj.add(horizontalBorder);
        }

        return sj.toString();
    }

    @Override
    public int getHeightRequirement(int width) {
        // The height requirement of a column is the sum of the height requirement of its rows
        int heightRequirement = 0;
        for (RowInterface row : rows) {
            heightRequirement += row.getHeightRequirement(width);
        }
        if (drawBorders) {
            heightRequirement += rows.size() + 1;
        }
        if (title != null) {
            heightRequirement++;
        }
        return heightRequirement;
    }

    private String renderTitle(int width) {
        String title = this.title;
        if (this.title == null) title = "";
        if (title.length() > width) {
            return title.substring(0, width);
        }
        int leftPadding = (width - title.length()) / 2;
        int rightPadding = width - leftPadding - title.length();
        title = padRight(title, rightPadding);
        title = padLeft(title, leftPadding);
        return title;
    }

    private static String padRight(String s, int n) {
        StringBuilder string = new StringBuilder(s);
        for (int i = 0; i < n; i++) {
            string.append(' ');
        }
        return string.toString();
    }

    private static String padLeft(String s, int n) {
        StringBuilder string = new StringBuilder(s);
        for (int i = 0; i < n; i++) {
            string.insert(i, ' ');
        }
        return string.toString();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
