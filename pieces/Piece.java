package pieces;

import actions.BoardPosition;
import java.util.*;

/** Piece class to represent Piece objects on the game board. */
public class Piece {

    // Fields to keep track of case, promoted pieces and String representations
    private boolean isUpper;
    private boolean isPromoted;
    private Piece original; // callback
    private char letter;

    // Constructs Piece object with case, promote capability and String representation
    protected Piece(boolean isUpper, char letter) {
        this(isUpper, false, letter, null);
        this.original = this; // Not allowed to pass "this" into super(...)
    }

    protected Piece(boolean isUpper, boolean isPromoted, char letter, Piece original) {
        this.isUpper = isUpper;
        this.isPromoted = isPromoted;
        this.original = original;
        this.letter = letter;
    }

    /* Returns List<BoardPosition> with the path starting with start and ending with end,
       or returns null if no path is available */
    public final List<BoardPosition> getPath(BoardPosition start, BoardPosition end) {
        int startRow = start.getRow();
        int startCol = start.getCol();
        int endRow = end.getRow();
        int endCol = end.getCol();


        // If it is an invalid move, return null
        if (!isValidMove(startRow, startCol, endRow, endCol)) {
            return null;
        }

        List<BoardPosition> path = new ArrayList<>();
        path.add(start);

        /* Continue to move along the path -- this algo should work for any Piece
           due to only moving one square at a time! */
        while (startRow != endRow || startCol != endCol) {
            int dirRow = Integer.signum(endRow - startRow);
            int dirCol = Integer.signum(endCol - startCol);
            startRow += dirRow;
            startCol += dirCol;

            path.add(new BoardPosition(startRow, startCol));
        }

        return path;
    }

    // Overwrite for each specific Piece's movement capabilities
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        return false;
    }

    // Returns whether or not piece can be promoted
    public boolean canPromote() {
        return !isPromoted;
    }

    // Returns the promoted piece
    public Piece upgrade() {
        return null;
    }

    // Returns whether or not Piece belongs to UPPER (useful for determining whose turn it is)
    public final boolean isUpper() {
        return isUpper;
    }

    // Returns the character representation of the Piece on the board
    public final char getLetter() {
        return letter;
    }

    // Returns original "unpromoted" version of Piece if promoted
    public final Piece getOriginal() {
        return this.original;
    }

    // When you capture a piece, the piece now belongs to other player
    public final void switchOwner() {
        isUpper = !isUpper;
    }

    // Returns String representation of the Piece, incuding if Piece is promoted
    public final String toString() {
        String response = "";
        if (this.isUpper()) {
            response = "" + Character.toUpperCase(letter);
        } else {
            response = "" + letter;
        }
        return (this.isPromoted) ?  "+" + response : response;
    }

}
