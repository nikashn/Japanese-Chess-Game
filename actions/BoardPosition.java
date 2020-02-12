package actions;

/** BoardPosition class to represent a BoardPosition object.
 *  BoardPositions make it easier to now represent a location on the board.
 */
public class BoardPosition {
    private int row;
    private int col;

    // Constructs new BoardPosition object given a position String
    public BoardPosition(String pos) {
        this.row = pos.charAt(0) - 'a';
        this.col = pos.charAt(1) - '1';
    }

    // Constructs new BoardPosition object given integers row and col
    public BoardPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Returns row for row, col pair
    public int getRow() {
        return row;
    }

    // Returns column for row, col pair
    public int getCol() {
        return col;
    }

    // Checks if row is within boundaries of board
    public boolean isRowInRange(int upper) {
        return 0 <= row && row < upper;
    }

    // Checks if column is within boundaries of board
    public boolean isColInRange(int upper) {
        return 0 <= col && col < upper;
    }

    // Returns String of board location
    public String toString() {
        char letter = (char) ('a' + row);
        char number = (char) ('1' + col);

        return "" + letter + number;
    }

}
