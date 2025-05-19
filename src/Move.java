import java.util.HashMap;
import java.util.Map;

/**
 * Represents a snapshot of the game state at a particular moment.
 * Used for undo and redo functionality in the {@link History}.
 */
public class Move {
    // region Fields
    private final PlayerEntity currentPlayer;
    private final PlayerEntity opponent;
    private final Map<Integer, Node> boardSnapshot;
    private final int noMillTurnCounter;
    private final int timeLeft;
    // endregion

    /**
     * Creates a new Move capturing the current game state.
     *
     * @param currentPlayer     The player who performed the move.
     * @param opponent          The opposing player.
     * @param boardSnapshot     The snapshot of the board at the time of the move.
     * @param noMillTurnCounter The number of consecutive turns without a formed
     *                          mill.
     * @param timeLeft          The remaining time for the move in seconds.
     */
    public Move(PlayerEntity currentPlayer, PlayerEntity opponent, Map<Integer, Node> boardSnapshot,
                int noMillTurnCounter, int timeLeft) {
        this.currentPlayer = currentPlayer;
        this.opponent = opponent;
        this.boardSnapshot = boardSnapshot;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }

//    @Override
//    public boolean equals(Object obj) {
//        System.out.println("[DEBUG] equals() aufgerufen");
//
//        if (this == obj)
//            return true;
//        if (obj == null || getClass() != obj.getClass())
//            return false;
//
//        Move other = (Move) obj;
//
//        // Nur auf Boardzustand vergleichen (alternativ: auch Spieler/Phase mit
//        // vergleichen)
//        return boardEquals(this.boardSnapshot, other.boardSnapshot)
//                && currentPlayer.getName().equals(other.currentPlayer.getName())
//                && opponent.getName().equals(other.opponent.getName())
//                && noMillTurnCounter == other.noMillTurnCounter;
//    }
//
//    private boolean boardEquals(Map<Integer, Node> boardA, Map<Integer, Node> boardB) {
//        if (boardA.size() != boardB.size())
//            return false;
//
//        for (Integer id : boardA.keySet()) {
//            Node nodeA = boardA.get(id);
//            Node nodeB = boardB.get(id);
//
//            if (nodeA == null || nodeB == null)
//                return false;
//
//            Stone stoneA = nodeA.getStone();
//            Stone stoneB = nodeB.getStone();
//
//            // Wenn beide leer → passt
//            if (stoneA == null && stoneB == null)
//                continue;
//
//            // Wenn nur einer belegt → unterschiedlich
//            if (stoneA == null || stoneB == null) {
//                System.out.println("[DEBUG] Unterschied bei Node " + id + ": einer leer");
//                return false;
//            }
//
//            // Wenn unterschiedliche Owner → unterschiedlich
//            String ownerA = stoneA.getOwner() != null ? stoneA.getOwner().getName() : "";
//            String ownerB = stoneB.getOwner() != null ? stoneB.getOwner().getName() : "";
//            if (!ownerA.equals(ownerB)) {
//                System.out.println("[DEBUG] Unterschied bei Node " + id + ": owner " + ownerA + " vs " + ownerB);
//                return false;
//            }
//        }
//
//        return true;
//    }

    // region Getters

    /**
     * Returns The player who performed this move.
     *
     * @return The current player.
     */
    public PlayerEntity getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the opponent at the time this move was performed.
     *
     * @return The opponent.
     */
    public PlayerEntity getOpponent() {
        return opponent;
    }

    /**
     * Returns the snapshot of the board when this move was made.
     *
     * @return A map representing the board state.
     */
    public Map<Integer, Node> getBoardSnapshot() {
//        Map<Integer, Node> copy = new HashMap<>();
//        for (Map.Entry<Integer, Node> entry : boardSnapshot.entrySet()) {
//            copy.put(entry.getKey(),
//                    entry.getValue().deepCopy(currentPlayer.deepCopy(),
//                            opponent.deepCopy()));
//        }
//        return copy;

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
