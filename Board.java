import actions.*;
import pieces.*;

import java.util.*;

/**
 * Class to represent Shogi Japanese Chess board
 */

public class Board {

    Piece[][] board;
    final int BOARD_SIZE = 5;

    // Movement fields and keeping track of game rules
    private static final int MAX_MOVES = 400;
    private int turn = 0;
    private boolean illegalMove;
    private boolean checkMated;

    // Captures fields
    private List<Piece> capturedByUpper, capturedByLower;

    // Initializes board object
    public Board() {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];

        // UPPER player
        board[0][4] = new Rook(true);
        board[1][4] = new Bishop(true);
        board[2][4] = new Silver(true);
        board[3][4] = new Gold(true);
        board[4][4] = new King(true);
        board[4][3] = new Pawn(true);

        // lower player
        board[0][1] = new Pawn(false);
        board[0][0] = new King(false);
        board[1][0] = new Gold(false);
        board[2][0] = new Silver(false);
        board[3][0] = new Bishop(false);
        board[4][0] = new Rook(false);

        // Captures
        capturedByUpper = new ArrayList<>();
        capturedByLower = new ArrayList<>();
    }

    // Initialize board object for test file mode/test cases
    public Board(Utils.TestCase tc) {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];

        // Place pieces on the board
        List<Utils.InitialPosition> placements = tc.initialPieces;
        for (Utils.InitialPosition placement : placements) {
            BoardPosition pos = new BoardPosition(placement.position);
            Piece piece = this.makeNewPiece(placement.piece);
            this.setAt(pos, piece);
        }

        // Captures lists
        capturedByUpper = new ArrayList<>();
        capturedByLower = new ArrayList<>();

        // Add the captures from test cases
        for (String cap : tc.upperCaptures) {
            Piece p = this.makeNewPiece(cap);
            if (p != null) {
                capturedByUpper.add(p);
            }
        }
        for (String cap : tc.lowerCaptures) {
            Piece p = this.makeNewPiece(cap);
            if (p != null) {
                capturedByLower.add(p);
            }
        }
    }

    // Checks whether the current player is in check
    public boolean inCheck() {
        // Find the position of the king
        BoardPosition kingLocation = new BoardPosition(-1, -1); // INVALID!
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece instanceof King && piece.isUpper() == isUpperTurn()) {
                    kingLocation = new BoardPosition(row, col);
                }
            }
        }

        // Can enemies pieces reach the king? If so, in check!
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                // Look at each of their pieces
                Piece piece = board[row][col];
                if (piece != null && piece.isUpper() != isUpperTurn()) {
                    BoardPosition currentLocation = new BoardPosition(row, col);
                    // Can this enemy piece reach the king?
                    if (isValidPath(piece.getPath(currentLocation, kingLocation))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Returns list of possible moves to get out of check
    public List<Action> escapeCheck() {
        List<Action> actions = new ArrayList<>();

        // Try dropping each piece in different places
        List<Piece> drops = (isUpperTurn()) ? capturedByUpper : capturedByLower;
        for (Piece piece : drops) {

            // Look at every possible position to drop it
            for (int dropRow = 0; dropRow < BOARD_SIZE; dropRow++) {
                for (int dropCol = 0; dropCol < BOARD_SIZE; dropCol++) {
                    BoardPosition dropLoc = new BoardPosition(dropRow, dropCol);

                    // Found a possible move
                    Drop possibleDrop = new Drop(piece.toString(), dropLoc.toString());
                    if (this.isValidDrop(possibleDrop)) {
                        // We can drop the piece
                        setAt(dropLoc, piece);

                        // See if it is still in check
                        if (!inCheck()) {
                            actions.add(possibleDrop);
                        }

                        // Finally move back the piece
                        setAt(dropLoc, null);
                    }
                }
            }
        }

        // Look at every piece on the board
        for (int startRow = 0; startRow < BOARD_SIZE; startRow++) {
            for (int startCol = 0; startCol < BOARD_SIZE; startCol++) {

                // If there is no piece here or it is an enemy piece, then skip over it
                BoardPosition startLoc = new BoardPosition(startRow, startCol);
                Piece piece = getAt(startLoc);
                if (piece == null || piece.isUpper() != isUpperTurn()) {
                    continue;
                }

                // Look at every possible position to move it
                for (int endRow = 0; endRow < BOARD_SIZE; endRow++) {
                    for (int endCol = 0; endCol < BOARD_SIZE; endCol++) {
                        BoardPosition endLoc = new BoardPosition(endRow, endCol);

                        // Found a possible move,
                        Move possibleMove = new Move(startLoc.toString(), endLoc.toString());
                        if (this.isValidMove(possibleMove)) {
                            // Move the piece
                            Piece oldEnd = getAt(endLoc);
                            setAt(endLoc, piece);
                            setAt(startLoc, null);

                            // See if it is still in check
                            if (!inCheck()) {
                                actions.add(possibleMove);
                            }

                            // Then move back the piece
                            setAt(startLoc, piece);
                            setAt(endLoc, oldEnd);
                        }
                    }
                }
            }
        }

        if (actions.isEmpty() && !isIllegalMove()) {
            checkMated = true;
        }

        return actions;
    }

    // Performs a move or a drop
    public boolean makeAction(Action action) {
        if (action == null) {
            illegalMove = true;
            return false;
        }

        boolean succeeded = false;
        if (action instanceof Move) {
            Move m = (Move) action;
            succeeded = makeMove(m);
        } else if (action instanceof Drop) {
            Drop d = (Drop) action;
            succeeded = makeDrop(d);
        }

        // Successfully performed a move, on to next turn
        if (succeeded) {
            turn++;
        }

        return succeeded;
    }

    // Performs a move, takes in a Move object
    private boolean makeMove(Move m) {
        BoardPosition startPosition = m.getStartPosition();
        BoardPosition endPosition = m.getEndPosition();

        // Take care of illegal cases
        if (!this.isValidMove(m)) {
            illegalMove = true;
            return false;
        }

        // Handle capturing pieces
        Piece capturedPiece = null;
        List<Piece> captured = (isUpperTurn()) ? capturedByUpper : capturedByLower;
        if (isOccupied(endPosition)) {
            capturedPiece = this.getAt(endPosition).getOriginal();
            capturedPiece.switchOwner();
            captured.add(capturedPiece);
        }

        // Moving a piece to unoccupied square
        Piece piece = this.getAt(startPosition);
        this.setAt(startPosition, null);
        if (m.isPromoteMove()) {
            piece = piece.upgrade();
        }
        this.setAt(endPosition, piece);

        // If you move into check, this is illegal
        if (inCheck()) {
            setAt(startPosition, piece);
            setAt(endPosition, capturedPiece);
            captured.remove(capturedPiece);

            illegalMove = true;
            return false;
        }

        return true;
    }

    // Performs a drop, takes in a drop object
    private boolean makeDrop(Drop d) {
        if (!isValidDrop(d)) {
            illegalMove = true;
            return false;
        }

        Piece dropped = null;
        String piece = d.getPieceName();
        int loc = 0;
        List<Piece> captured = (isUpperTurn()) ? capturedByUpper : capturedByLower;
        Iterator<Piece> iterator = captured.iterator();
        while (iterator.hasNext()) {
            Piece p = iterator.next();
            if (p.toString().equalsIgnoreCase(piece)) { // check that dropping piece is in captured
                dropped = p;
                setAt(d.getPosition(), dropped);
                iterator.remove();
                break;
            }
            loc++;
        }

        // Cannot checkmate with Pawn
        if (dropped instanceof Pawn) {
            turn++;
            if (this.inCheck()) {
                List<Action> actions = escapeCheck();
                if (actions.isEmpty()) {
                    // Undo the action
                    captured.add(loc, getAt(d.getPosition()));
                    setAt(d.getPosition(), null);

                    turn--;
                    illegalMove = true;
                    return false;
                }
            }
            turn--;
        }

        return true;
    }

    // Returns whether or not move is valid
    private boolean isValidMove(Move m) {

        // Handle illegal moves
        // 1. Move it off board
        // 2. Moving nothing
        // 3. Cannot move other player's piece
        // 4. Try to capture own piece (moving to same spot)
        // 5. Can it be promoted?
        // 6. Is it in promotion zone?
        // 7. Can piece move that way?
        // 8. Was Pawn promoted once in promotion zone?
        // 9. Is it reachable on the board (are there pieces in the way)?

        BoardPosition startPosition = m.getStartPosition();
        BoardPosition endPosition = m.getEndPosition();

        // 1. Move it off board
        if (!startPosition.isRowInRange(BOARD_SIZE) || !startPosition.isColInRange(BOARD_SIZE) ||
                !endPosition.isRowInRange(BOARD_SIZE) || !endPosition.isColInRange(BOARD_SIZE)) {
            return false;
        }

        // 2. Moving nothing
        if (!this.isOccupied(startPosition)) {
            return false;
        }

        // 3. Moving other player's piece
        String player = this.getCurrentPlayer();
        Piece piece = this.getAt(startPosition);
        if (this.isUpperTurn() != piece.isUpper()) {
            return false;
        }

        // 4. Try to capture own piece
        if (this.isOccupied(endPosition) && this.isUpperTurn() == this.getAt(endPosition).isUpper()) {
            return false;
        }

        // 5. Can the piece be promoted?
        if (m.isPromoteMove() && !piece.canPromote()) {
            return false;
        }

        // 6. Is it moving in, within or out of promotion zone
        if (m.isPromoteMove()) {
            int proZone = (this.isUpperTurn()) ? 0 : BOARD_SIZE - 1;
            if (startPosition.getCol() != proZone && endPosition.getCol() != proZone) {
                System.err.println("Not in promotion zone. ");
                return false;
            }
        }

        // 7. Can specific piece move this way?
        if (!piece.isValidMove(startPosition.getRow(), startPosition.getCol(),
                endPosition.getRow(), endPosition.getCol())) {
            return false;
        }

        // 8. Pawn pieces must be promoted once they reach the furthest row (otw illegal)
        int proZone = (this.isUpperTurn()) ? 0 : BOARD_SIZE - 1;
        if (endPosition.getCol() == proZone && piece instanceof Pawn && !m.isPromoteMove()) {
            setAt(startPosition, piece.upgrade());
        }

        // 9. Another piece along path of movement
        List<BoardPosition> path = piece.getPath(startPosition, endPosition);
        if (!isValidPath(path)) {
            return false;
        }

        return true;
    }

    // Returns whether or not drop is valid
    private boolean isValidDrop(Drop d) {

        // Handle illegal moves
        // 1. Drop it off board
        // 2. Dropping on occupied square
        // 3. Dropping a piece not in your captured list
        // 4. Cannot drop Pawn piece onto same column as another Pawn
        // 5. Pawn cannot be dropped into promotion zone

        BoardPosition position = d.getPosition();

        // 1. Drop it off board
        if (!position.isRowInRange(BOARD_SIZE) && !position.isColInRange(BOARD_SIZE)) {
            return false;
        }

        // 2. Drop it on occupied square
        if (isOccupied(position)) {
            return false;
        }

        // 3. Dropping a piece not in your captured list
        boolean contains = false;
        List<Piece> captures = (isUpperTurn()) ? capturedByUpper : capturedByLower;
        for (Piece p : captures) {
            if (p.toString().equalsIgnoreCase(d.getPieceName())) {
                contains = true;
            }
        }
        if (!contains) {
            return false;
        }

        // 4. Pawn cannot be dropped into a column with another Pawn
        String piece = d.getPieceName();
        if (piece.equalsIgnoreCase("p")) {

            // Search for a pawn in the same column
            int pRow = position.getRow();
            for (int i = 0; i < BOARD_SIZE; i++) {
                Piece testPiece = board[pRow][i];
                if (testPiece instanceof Pawn && testPiece.isUpper() == isUpperTurn()) {
                    return false;
                }
            }
        }

        // 5. Pawn cannot be dropped into promotion zone
        if (piece.equalsIgnoreCase("p")) {
            int proZone = (this.isUpperTurn()) ? 0 : BOARD_SIZE - 1;
            if (position.getCol() == proZone) {
                return false;
            }
        }

        return true;
    }

    // Checks if path on board is available - Are there pieces in the way?
    private boolean isValidPath(List<BoardPosition> path) {
        if (path == null) {
            return false;
        }

        // Look at every location along the path
        for (int i = 1; i < path.size() - 1; i++) {
            if (this.isOccupied(path.get(i))) {
                return false;
            }
        }

        return true;
    }

    // Returns list of captured pieces by UPPER
    public List<Piece> capturesUpper() {
        return new ArrayList<>(capturedByUpper);
    }

    // Returns list of captures pieces by lower
    public List<Piece> capturesLower() {
        return new ArrayList<>(capturedByLower);
    }

    // Whose turn is it?
    public boolean isUpperTurn() {
        return (turn % 2 == 1);
    }

    // Checks if game is over due to total move count
    public boolean hasRemainingMoves() {
        return turn < MAX_MOVES;
    }

    // Checks if game is over due to check mate
    public boolean isCheckMated() {
        return this.checkMated;
    }

    // Checks if game is over due to an illegal move
    public boolean isIllegalMove() {
        return this.illegalMove;
    }

    // Returns whether or not game has ended via illegal move, checkmate or move count
    public boolean gameOver() {
        return  isIllegalMove() || isCheckMated() || !hasRemainingMoves();
    }

    // Gets the current player
    public String getCurrentPlayer() {
        if (turn % 2 == 0) {
            return "lower";
        }
        return "UPPER";
    }

    // Gets the opponent player
    public String getOtherPlayer() {
        if (turn % 2 == 0) {
            return "UPPER";
        }
        return "lower";
    }

    // Checks if square on board is occupied
    private boolean isOccupied(int col, int row) {
        return board[col][row] != null;
    }

    // Checks if square on board is occupied (using BoardPosition object for convenience)
    private boolean isOccupied(BoardPosition bp) {
        return isOccupied(bp.getRow(), bp.getCol());
    }

    // Gets the piece at specified position
    private Piece getAt(BoardPosition bp) {
        int row = bp.getRow(), col = bp.getCol();
        return board[row][col];
    }

    // Sets a new piece location on board
    private void setAt(BoardPosition bp, Piece p) {
        board[bp.getRow()][bp.getCol()] = p;
    }

    // Returns Piece object from a pieces String representation
    private Piece makeNewPiece(String name) {

        if (name == null || name.isEmpty()) {
            return null;
        }

        char last = name.charAt(name.length() - 1);
        boolean isUpper = Character.isUpperCase(last);
        last = Character.toLowerCase(last);

        Piece p = null;
        switch (last) {
            case 'p':
                p = new Pawn(isUpper);
                break;
            case 'd':
                p = new King(isUpper);
                break;
            case 's':
                p = new Gold(isUpper);
                break;
            case 'r':
                p = new Silver(isUpper);
                break;
            case 'g':
                p = new Bishop(isUpper);
                break;
            case 'n':
                p = new Rook(isUpper);
                break;
            default:
                throw new IllegalArgumentException("Invalid piece: " + name);
        }

        // Promoted pieces represented by a '+' in front of their String
        if (name.charAt(0) == '+') {
            p = p.upgrade();
        }

        return p;
    }

    /* Print board */
    public String toString() {
        String[][] pieces = new String[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece curr = (Piece) board[col][row];
                pieces[col][row] = this.isOccupied(col, row) ? board[col][row].toString() : "";
            }
        }
        return stringifyBoard(pieces);
    }
    private String stringifyBoard(String[][] board) {
        String str = "";

        for (int row = board.length - 1; row >= 0; row--) {

            str += Integer.toString(row + 1) + " |";
            for (int col = 0; col < board[row].length; col++) {
                str += stringifySquare(board[col][row]);
            }
            str += System.getProperty("line.separator");
        }

        str += "    a  b  c  d  e" + System.getProperty("line.separator");

        return str;
    }
    private String stringifySquare(String sq) {
        switch (sq.length()) {
            case 0:
                return "__|";
            case 1:
                return " " + sq + "|";
            case 2:
                return sq + "|";
        }

        throw new IllegalArgumentException("Board must be an array of strings like \"\", \"P\", or \"+P\"");
    }

}

