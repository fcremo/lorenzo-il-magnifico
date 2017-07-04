package ui.cli.layout.table;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents a column in a tabular layout
 */
public class TextBox implements ColumnInterface, RowInterface {
    /**
     * The weight of the column
     */
    private int weight;

    /**
     * The text displayed inside the column
     */
    private String text;

    /**
     * Builds a column with the given text and weight
     * @param text
     * @param weight
     */
    public TextBox(String text, int weight) {
        this.text = text;
        this.weight = weight;
    }

    /**
     * Builds a column with the given text and weight 1
     * @param text
     */
    public TextBox(String text) {
        this.text = text;
        this.weight = 1;
    }

    @Override
    public String render(int width, int height) {
        String reflowed = reflowText(text, width);

        StringBuilder tmp = new StringBuilder();

        int numRows = 0;

        for(String row : reflowed.split("\n")) {
            tmp.append(row);
            // Pad the remaining space
            for(int i=row.length(); i<width; i++) {
                tmp.append(" ");
            }
            tmp.append("\n");
            numRows++;
        }

        // Create an empty row of the right width
        String emptyRow = new String(new char[width]).replace("\0", " ") + "\n";
        // Fill the table row with empty text rows
        for(int row=numRows; row<height; row++) {
            tmp.append(emptyRow);
        }

        return tmp.toString();
    }

    /**
     * Reflow the text so that it fits a given maxWidth
     * @param originalText
     * @param maxWidth
     */
    private String reflowText(String originalText, int maxWidth) {
        StringBuilder sb = new StringBuilder();

        List<String> sections = Arrays.asList(originalText.split("\n"));
        for(String section : sections){
            int remainingWidth = maxWidth;

            for(String token : section.split(" ")) {
                if (token.length() > remainingWidth) {
                    sb.append("\n");
                    remainingWidth = maxWidth;
                }
                sb.append(token);
                sb.append(" ");
                remainingWidth = remainingWidth - token.length() - 1;
            }
            sb.append("\n");
        }


        return sb.toString();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
