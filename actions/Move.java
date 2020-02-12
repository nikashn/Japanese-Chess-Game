package actions;

/** Move class to depict a movement of a piece on the board
 *  from start location to end location.
 */
public class Move extends Action {
    private BoardPosition startPosition;
    private BoardPosition endPosition;

    private boolean promote;

    // Constructs Move object with start and end locations
    public Move(String start, String end) {
        this(start, end, false);
    }

    // Constructs Move object with start and end locations as well as promotion ability
    public Move(String start, String end, boolean promote) {
        this.startPosition = new BoardPosition(start);
        this.endPosition = new BoardPosition(end);
        this.promote = promote;
    }

    // Checks whether piece will be promoted
    public boolean isPromoteMove() {
        return promote;
    }

    // Return BoardPosition object of starting position on board
    public BoardPosition getStartPosition() {
        return startPosition;
    }

    // Return BoardPosition object of ending position on board
    public BoardPosition getEndPosition() {
        return endPosition;
    }

    // Returns String of movement
    public String toString() {
        String response = "move " + startPosition + " " + endPosition;
        if (isPromoteMove()) {
            return response + " promote";
        }
        return response;
    }

}
