package pieces;

public class BishopPro extends Piece {

    private Bishop bishop;
    private King king;

    // BishopPro object now contains Bishop or King capabilities
    protected BishopPro(boolean isUpper, boolean isPromoted, char letter, Piece original) {
        super(isUpper, isPromoted, letter, original);
        this.bishop = new Bishop(isUpper);
        this.king = new King(isUpper);
    }

    // Bishops can now move like Bishops or Kings
    public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        return bishop.isValidMove(startRow, startCol, endRow, endCol) ||
                king.isValidMove(startRow, startCol, endRow, endCol);
    }

    // Cannot promote an already promoted Piece
    public boolean canPromote() {
        return false;
    }

}
