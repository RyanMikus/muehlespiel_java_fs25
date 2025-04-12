public class RuleManager {
    private static RuleManager instance;
    private static GameManager gameManager;

    private RuleManager() {
        gameManager =  GameManager.getInstance();
    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static RuleManager getInstance() {
        if(instance == null) {
            instance = new RuleManager();
        }
        return instance;
    }

    /**/
    public boolean isMoveValid() {
        return false;
    }

    /**/
    public boolean isMillFormed() {
        // reset noMillTurnCounter in GameManager if mill is formed!
        return false;
    }

    /**
     * Checks if the game is over and sets the winner if so.
     * 1. A player has only 2 stones left (is in GameOverPhase)
     * 2. There was no mill formed for 20 turns.
     * 3. A player can't move a stone to a valid position anymore.
     * 4. Timer ran out.
     *
     * @param playerWhite The first player.
     * @param playerBlack The second player.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver(PlayerEntity playerWhite, PlayerEntity playerBlack) {
        // Player white lost
        if(isPlayerGameOver(playerWhite) || isPlayerBlocked(playerWhite)) {
            gameManager.winner = GameManager.Winner.BLACK;
            return true;
        }
        // Player black lost
        if(isPlayerGameOver(playerBlack) || isPlayerBlocked(playerBlack)) {
            gameManager.winner = GameManager.Winner.WHITE;
            return true;
        }

        // Draw
        if(isStalemate()) {
            gameManager.winner = GameManager.Winner.DRAW;
            return true;
        }

        return false;
    }

    /**
     * Checks if the player is in the game over phase (only 2 stones left)
     *
     * @param player The player that gets checked.
     * @return true if the player is in game over phase, false otherwise.
     */
    private boolean isPlayerGameOver(PlayerEntity player) {
        return player.getPhase() instanceof GameOverPhase;
    }

    /**
     * Checks if the player can do any valid moves (is blocked).
     *
     * @param player The player that gets checked.
     * @return true if the player can't do any valid moves anymore, false otherwise.
     */
    public boolean isPlayerBlocked(PlayerEntity player) {
        if(!(player.getPhase() instanceof MovingPhase)) {
            return false;
        }

        for(Stone stone : player.getRemainingStones()) {
            for(Node neighbor : stone.getNode().getNeighbors()) {
                if(!(neighbor.isOccupied())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks how many turns no mill was formed for.
     *
     * @return true if there was no mill for 20 turns, false otherwise.
     */
    public boolean isStalemate() {
        return GameManager.getInstance().noMillTurnCounter == 20;
    }

    /**/
    public boolean isTimeOver() {
        return false;
    }
}
