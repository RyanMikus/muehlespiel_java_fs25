public class RuleManager {
    private static RuleManager instance;
    private BoardManager boardManager;
    private GameManager gameManager;

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

    /**
     * Setter for the boardManager to avoid recursive constructor calls.
     *
     * @param boardManager The instance of the board manager.
     */
    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
    }

    /**
     * Setter for the gameManager to avoid recursive constructor calls.
     *
     * @param gameManager The instance of the game manager.
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Checks if the intended move is valid for the current game phase.
     *
     * @param player   The player that intends to perform a move.
     * @param fromNode The origin node (can be null in SettingPhase).
     * @param toNode   The target node.
     * @return true if move is valid, false otherwise.
     */
    public boolean isMoveValid(PlayerEntity player, Node fromNode, Node toNode) {
        // General possible errors
        String errors = "";

        if(fromNode != null) {
            if(!fromNode.isOccupied())
                errors += "No stone is placed on that node (id=" + fromNode.getID() + ")!\n";
            if(fromNode.getStone().getOwner() != player)
                errors += "The stone on this node (id=" + fromNode.getID()
                        + ") does not belong to the player whose turn it is (" + player.getName() + "!\n";
        }
        if(fromNode == toNode)
            errors += "You can not place a stone on to itself!\n";
        if(fromNode != toNode && toNode.isOccupied())
            errors += "You can not place a stone on to another stone!\n";

        // Errors specific to phases
        GamePhase currentPhase = player.getPhase();

        if(currentPhase instanceof SettingPhase) {
            // More errors might come up.
        } else if(currentPhase instanceof MovingPhase && fromNode != null) {
            if(!fromNode.getNeighbors().contains(toNode))
                errors += "Can only move to neighbors!\nA stone can not be moved from " + fromNode.getID() + " to "
                        + toNode.getID() + "!\n";
        } else if(currentPhase instanceof JumpingPhase) {
            // More errors might come up.
        } else if(currentPhase instanceof GameOverPhase) {
            errors += "Game is already over. You must not make moves!";
        } else {
            errors += "Player is not in a legal phase! Player " + player.getName() + " is in phase "
                    + player.getPhase().getClass().getSimpleName() + "\n";
        }

        // No errors occured
        if(errors.isEmpty())
            return true;

        if (player instanceof Player)
            System.out.print(errors);
        return false;
    }

    public boolean isRemovalValid(PlayerEntity remover, Node targetNode) {
        boolean removalValid = false;
        Stone targetStone;
        PlayerEntity opponent;

        // Only remove stone, if the targetNode is valid
        if (targetNode.isOccupied() && targetNode.getStone().getOwner() == remover) {
            targetStone = targetNode.getStone();
            opponent = targetStone.getOwner();
            boolean freeStones = false;

            // Check if the opponent has a stone, that is not part of a mill
            for (Stone stone : opponent.getRemainingStones()) {
                if (countMillsFormed(stone) == 0) {
                    freeStones = true;
                    break;
                }
            }

            // Remove stone only if the target is not part of a mill
            // Exception if all remaining stones of the opponent are part of a mill
            if (countMillsFormed(targetStone) == 0 || !freeStones)
                removalValid = true;

            // TODO:
            // The following logic might want to be implemented in another method. Reason
            // for this being that if the opponent has 3 stones remaining, the opponent
            // should automatically lose without a player needing to take away 1 or 2
            // stones. This could be implemented in the method placeStone. If so, placeStone
            // would need access to both players.
            // opponent.getPhase().nextPhase();
        }
        return removalValid;
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
        int placedPos = lastPlacedStone.getNode().getID();
        int millCount = 0;

        for(int[] mill : boardManager.potentialMills) {
            // Extract potential mills that contain the position of the newly placed stone
            boolean includesPlaced = false;
            for(int pos : mill) {
                if(pos == placedPos) {
                    includesPlaced = true;
                    break;
                }
            }
            if(!includesPlaced)
                continue;

            // Check if all members of the potential mill are possessed by the current player
            boolean allOwned = true;
            for(int pos : mill) {
                Node node = boardManager.getNode(pos);
                if(!node.isOccupied() || node.getStone().getOwner() != player) {
                    allOwned = false;
                    break;
                }
            }

            // Add one formed mill to the counter
            if(allOwned) {
                //debug
                //System.out.println("Mühle erkannt");

                millCount++;
                if(millCount > 2) {
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
    public boolean isGameOver(PlayerEntity playerWhite, PlayerEntity playerBlack) {
        // Player black won
        if(isPlayerGameOver(playerWhite) || isPlayerBlocked(playerWhite)) {
            gameManager.setWinner(playerBlack);
            return true;
        }
        // Player white won
        if(isPlayerGameOver(playerBlack) || isPlayerBlocked(playerBlack)) {
            gameManager.setWinner(playerWhite);
            return true;
        }

        // Draw
        if(isStalemate()) {
            gameManager.setWinner(null);
            return true;
        }

        return false;
    }

    //region Helpermethods GameOver
    /**
     * Checks if the player is in the game over phase (only 2 stones left).
     * Sets the loss cause to STONECOUNT if so.
     *
     * @param player The player that gets checked.
     * @return true if the player is in game over phase, false otherwise.
     */
    private boolean isPlayerGameOver(PlayerEntity player) {
        boolean isGameOver = player.getPhase() instanceof GameOverPhase;
        if(isGameOver) {
            gameManager.lossCause = GameManager.LossCause.STONECOUNT;
        }
        return isGameOver;
    }

    /**
     * Checks if the player can perform any valid moves (is blocked).
     * Sets the loss cause to BLOCKED if the player is blocked.
     *
     * @param player The player that gets checked.
     * @return true if the player can't do any valid moves anymore, false otherwise.
     */
    public boolean isPlayerBlocked(PlayerEntity player) {
        // Player can only be blocked in the moving phase
        if(!(player.getPhase() instanceof MovingPhase)) {
            return false;
        }

        // Check for each stone if a neighbor node is occupied
        for(Stone stone : player.getRemainingStones()) {
            for(Node neighbor : stone.getNode().getNeighbors()) {
                if(!(neighbor.isOccupied())) {
                    return false;
                }
            }
        }

        gameManager.lossCause = GameManager.LossCause.BLOCKED;
        return true;
    }

    /**
     * Checks how many turns no mill was formed for.
     * Sets the loss cause to STALEMATE if there was no mill for 20 turns.
     *
     * @return true if there was no mill for 20 turns, false otherwise.
     */
    public boolean isStalemate() {
        boolean stalemate = gameManager.getNoMillTurnCounter() >= 20;
        if(stalemate) {
            gameManager.lossCause = GameManager.LossCause.STALEMATE;
        }
        return stalemate;
    }
    // endregion
}