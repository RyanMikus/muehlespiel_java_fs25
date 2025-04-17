public class RuleManager {
    private static RuleManager instance;
    private static BoardManager boardManager;
    private static GameManager gameManager;

    /**
     * Creates a new RuleManager and initializes the other managers, by getting their instance.
     */
    private RuleManager() {
        boardManager = BoardManager.getInstance();
        gameManager = GameManager.getInstance();
    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static RuleManager getInstance() {
        if (instance == null) {
            instance = new RuleManager();
        }
        return instance;
    }

    /**/
    public boolean isMoveValid() {
        return false;
    }

    /**
     * Only gets called when a move is made, otherwise multiple mills would be
     * formed.
     * 
     * @param lastPlacedStone The stone that potentially formed one or two mills.
     * @return The amount of new mills formed.
     * @throws IllegalStateException if the amount of newly formed mills is greater
     *                               than 2.
     */
    public int countNewMillsFormed(Stone lastPlacedStone) {
        PlayerEntity player = lastPlacedStone.getOwner();
        int placedPos = lastPlacedStone.getNode().getID(); // Requires Node to have getIndex()
        int millCount = 0;

        for (int[] mill : boardManager.potentialMills) {
            boolean includesPlaced = false;
            for (int pos : mill) {
                if (pos == placedPos) {
                    includesPlaced = true;
                    break;
                }
            }
            if (!includesPlaced)
                continue;

            boolean allOwned = true;
            for (int pos : mill) {
                Node node = boardManager.getNode(pos);
                if (!node.isOccupied() || node.getStone().getOwner() != player) {
                    allOwned = false;
                    break;
                }
            }

            if (allOwned) {
                millCount++;
                if (millCount > 2) {
                    throw new IllegalStateException("More than 2 mills formed — invalid state.");
                }
            }
        }

        return millCount;
    }

    /**
     * Checks if the game is over and sets the winner if so.
     * 1. A player has only 2 stones left (is in GameOverPhase).
     * 2. There was no mill formed for 20 turns.
     * 3. A player can't move a stone to a valid position anymore (is in
     * MovingPhase).
     * 4. Timer ran out.
     *
     * @param playerWhite The first player.
     * @param playerBlack The second player.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver(PlayerEntity playerWhite, PlayerEntity playerBlack, PlayerEntity currentPlayer) {
        // Player black won
        if (isPlayerGameOver(playerWhite) || isPlayerBlocked(playerWhite)) {
            gameManager.setWinner(playerBlack);
            return true;
        }
        // Player white won
        if (isPlayerGameOver(playerBlack) || isPlayerBlocked(playerBlack)) {
            gameManager.setWinner(playerWhite);
            return true;
        }

        // Draw
        if (isStalemate()) {
            gameManager.setWinner(null);
            return true;
        }

        return false;
    }

    //region Helpermethods GameOver
    /**
     * Checks if the player is in the game over phase (only 2 stones left)
     *
     * @param player The player that gets checked.
     * @return true if the player is in game over phase, false otherwise.
     */
    private boolean isPlayerGameOver(PlayerEntity player) {
        gameManager.lossCause = GameManager.LossCause.STONECOUNT;
        return player.getPhase() instanceof GameOverPhase;
    }

    /**
     * Checks if the player can do any valid moves (is blocked).
     *
     * @param player The player that gets checked.
     * @return true if the player can't do any valid moves anymore, false otherwise.
     */
    public boolean isPlayerBlocked(PlayerEntity player) {
        if (!(player.getPhase() instanceof MovingPhase)) {
            return false;
        }

        for (Stone stone : player.getRemainingStones()) {
            for (Node neighbor : stone.getNode().getNeighbors()) {
                if (!(neighbor.isOccupied())) {
                    return false;
                }
            }
        }

        gameManager.lossCause = GameManager.LossCause.BLOCKED;
        return true;
    }

    /**
     * Checks how many turns no mill was formed for.
     *
     * @return true if there was no mill for 20 turns, false otherwise.
     */
    public boolean isStalemate() {
        gameManager.lossCause = GameManager.LossCause.STALEMATE;
        return GameManager.getInstance().getNoMillTurnCounter() >= 20;
    }
    //endregion
}