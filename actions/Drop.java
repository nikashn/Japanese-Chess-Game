package actions;

/** Drop class to depict a dropping of a piece on the board
 *  from a player's captured list.
 */
public class Drop extends Action {

    private String piece;
    private BoardPosition loc;

    // Constructs a Drop object with String of piece and dropping location
    public Drop(String piece, String loc) {
        int col = loc.charAt(1) - '1';
        int row = loc.charAt(0) - 'a';
        this.loc = new BoardPosition(row, col);
        this.piece = piece.toLowerCase();
    }

    // Returns BoardPosition object of dropping location
    public BoardPosition getPosition() {
        return loc;
    }

    // Returns String representation of the dropping piece
    public String getPieceName() {
        return piece;
    }

    // Returns Drop action String
    public String toString() {
        return "drop " + piece + " " + loc;
    }

}
