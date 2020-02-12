package pieces;

public class Silver extends Piece {

   // Uses Piece.java constructor to determine the case
   public Silver(boolean c) {
      super(c, 's');
   }

   // Returns new upgraded piece with new movement capabilities
   public Piece upgrade() {
      return new Gold(isUpper(), true, getLetter(), this);
   }

   // Can move 1 square any direction except horizontally or directly backward
   public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
      if (this.isUpper()) {
         return (endCol == startCol + 1) && Math.abs(endRow - startRow) == 1 ||
                 (endCol == startCol - 1) && Math.abs(endRow - startRow) <= 1;
      } else {
         return (endCol == startCol - 1) && Math.abs(endRow - startRow) == 1 ||
                 (endCol == startCol + 1) && Math.abs(endRow - startRow) <= 1;
      }
   }

}