package pieces;

public class Bishop extends Piece {

   // Uses Piece.java constructor to determine the case
   public Bishop(boolean c) {
      super(c, 'b');
   }

   // Returns new upgraded piece with new movement capabilities
   public Piece upgrade() {
      return new BishopPro(isUpper(), true, getLetter(), this);
   }

   // Bishops can move diagonally in all directions
   public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
      return Math.abs(endRow - startRow) == Math.abs(endCol - startCol);
   }

}