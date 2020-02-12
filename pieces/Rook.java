package pieces;

public class Rook extends Piece {

   // Uses Piece.java constructor to determine the case
   public Rook(boolean c) {
      super(c, 'r');
   }

   // Returns new upgraded piece with new movement capabilities
   public Piece upgrade() {
      return new RookPro(isUpper(), true, getLetter(), this);
   }

   // Rook can move any number of squares horizontal or vertical
   public boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
      return startRow == endRow || startCol == endCol;
   }

}