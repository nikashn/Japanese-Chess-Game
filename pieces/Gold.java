package pieces;

public class Gold extends Piece {

   // Uses Piece.java constructor to determine the case
   public Gold(boolean c) {
      super(c, 'g');
   }

   protected Gold(boolean isUpper, boolean isPromoted, char letter, Piece original) {
      super(isUpper, isPromoted, letter, original);
   }

   // Gold can move 1 square in any direction except backwards diagonal
   public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
      // different for UPPER and lower backspace
      if (this.isUpper()) {
         return (endCol == startCol + 1) && (endRow == startRow) ||
                 (endCol == startCol) && Math.abs(endRow - startRow) <= 1 ||
                 (endCol == startCol - 1) && Math.abs(endRow - startRow) <= 1;
      } else {
         return (endCol == startCol - 1) && (endRow == startRow) ||
                 (endCol == startCol) && Math.abs(endRow - startRow) <= 1 ||
                 (endCol == startCol + 1) && Math.abs(endRow - startRow) <= 1;
      }
   }

   // Golds cannot be promoted
   public boolean canPromote() {
      return false;
   }

}