package pieces;

public class King extends Piece {

   // Uses Piece.java constructor to determine the case
   public King(boolean c) {
      super(c, 'k');
   }

   // King can move 1 square in any direction
   public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
      return Math.abs(startRow - endRow) <= 1 && Math.abs(startCol - endCol) <= 1;
   }

   // Kings cannot be promoted
   public boolean canPromote() {
      return false;
   }

}