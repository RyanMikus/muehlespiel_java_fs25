import java.util.HashMap;
import java.util.Map;

/**
 * Represents a snapshot of the game state at a particular moment.
 * Used for undo and redo functionality in the {@link History}.
 */
public class Move {
    // region Fields
    private final int placedStonesWhite;
    private final int placedStonesBlack;
    private final Map<Integer, Node> boardSnapshot;
    private final int noMillTurnCounter;
    private final int timeLeft;
    // endregion

    /**
     * Creates a new Move capturing the current game state.
     *
     * @param boardSnapshot     The snapshot of the board at the time of the move.
     * @param noMillTurnCounter The number of consecutive turns without a formed mill.
     * @param timeLeft          The remaining time for the move in seconds.
     */
    public Move(int placedStonesWhite, int placedStonesBlack, Map<Integer, Node> boardSnapshot, int noMillTurnCounter, int timeLeft) {
        this.placedStonesWhite = placedStonesWhite;
        this.placedStonesBlack = placedStonesBlack;
        this.boardSnapshot = boardSnapshot;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }

    // region Getters

    /**
     * Returns the number of stones the white player has already placed.
     *
     * @return The amount of placed stones.
     */
    public int getPlacedStonesWhite() {
        return placedStonesWhite;
    }

    /**
     * Returns the number of stones the black player has already placed.
     *
     * @return The amount of placed stones.
     */
    public int getPlacedStonesBlack() {
        return placedStonesBlack;
    }

    /**
     * Returns the snapshot of the board when this move was made.
     *
     * @return A map representing the board state.
     */
    public Map<Integer, Node> getBoardSnapshot() {
        return boardSnapshot;
    }

    /**
     * Returns the number of turns without a formed mill at the time of this move.
     *
     * @return The no mill turn counter.
     */
    public int getNoMillTurnCounter() {
        return noMillTurnCounter;
    }

    /**
     * Returns the time left for the move in seconds.
     *
     * @return The remaining time.
     */
    public int getTimeLeft() {
        return timeLeft;
    }
    // endregion
}
