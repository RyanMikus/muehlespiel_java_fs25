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

    /**
     * Checks if the intended move is valid for the current game phase.
     * 
     * @param player   The player that intends to perform a move.
     * @param fromNode The origin node.
     * @param toNode   The target node.
     * @return true if move is valid, false if move is not valid
     */
    public boolean isMoveValid(PlayerEntity player, Node fromNode, Node toNode) {
        String errors = "";
        if (fromNode == toNode)
            errors += "You can not place a stone on to itself!\n";
        if (toNode.isOccupied())
            errors += "You can not place a stone on to another stone!\n";
        if (!fromNode.isOccupied())
            errors += "No stone is placed on that node (id=" + fromNode.getID() + ")!\n";
        if (fromNode.getStone().getOwner() != player)
            errors += "The stone on this node (id=" + fromNode.getID()
                    + ") does not belong to the player whose turn it is (" + player.getName() + "!\n";

        GamePhase currentPhase = player.getPhase();
        if (currentPhase instanceof SettingPhase) {
            // More errors might come up.
        } else if (currentPhase instanceof MovingPhase) {
            if (!fromNode.getNeighbors().contains(toNode))
                errors += "Can only move to neighbors!\nA stone can not be moved from " + fromNode.getID() + " to "
                        + toNode.getID() + "!\n";
        } else if (currentPhase instanceof JumpingPhase) {
            // More errors might come up.
        } else if (currentPhase instanceof GameOverPhase) {
            errors += "Game is already over. You may not make moves!";
        } else {
            errors += "Player is not in a legal phase! Player " + player.getName() + " is in phase "
                    + player.getPhase().getClass().toString() + "\n";
        }

        if (errors == "")
            return true;
        System.out.print(errors);
        return false;
    }

    /**
     * Counts the new mills formed with the newly placed or moved stone.
     * Only gets called when a move is made, otherwise multiple mills would be
     * formed.
     * 
     * @param lastPlacedStone The stone that potentially formed one or two mills.
     * @return The amount of new mills formed.
     * @throws IllegalStateException if the amount of newly formed mills is greater
     *                               than 2.
     */
    public int countMillsFormed(Stone lastPlacedStone) {
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