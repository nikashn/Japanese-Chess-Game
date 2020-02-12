package pieces;

public class RookPro extends Piece {

    private Rook rook;
    private King king;

    // RookPro object now contains Rook or King capabilities
    protected RookPro(boolean isUpper, boolean isPromoted, char letter, Piece original) {
        super(isUpper, isPromoted, letter, original);
        this.rook = new Rook(isUpper);
        this.king = new King(isUpper);
    }

    // Rook can now move like Rook OR King
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        return rook.isValidMove(startRow, startCol, endRow, endCol) ||
                king.isValidMove(startRow, startCol, endRow, endCol);
    }

    // Cannot promote an already promoted Piece
    public boolean canPromote() {
        return false;
    }

}
