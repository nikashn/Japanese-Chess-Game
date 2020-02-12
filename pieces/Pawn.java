package pieces;

public class Pawn extends Piece {

   // Uses Piece.java constructor to determine the case
   public Pawn(boolean c) {
      super(c, 'p');
   }

   // Returns new upgraded piece with new movement capabilities
   public Piece upgrade() {
      return new Gold(isUpper(), true, this.getLetter(), this);
   }

   // BoxPreview can move 1 square forward
   public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
      if (this.isUpper()) {
         return startCol - 1 == endCol && (startRow == endRow);
      } else {
         return startCol + 1 == endCol && (startRow == endRow);
      }
   }

}
