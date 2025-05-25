import java.util.ArrayList;
import java.util.List;

public class RuleManager {
    private static RuleManager instance;

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
     * @param fromNode The origin node (can be null in SettingPhase).
     * @param toNode   The target node.
     * @return true if move is valid, false otherwise.
     */
    public boolean isMoveValid(PlayerEntity player, Node fromNode, Node toNode) {
        GameManager gameManager = GameManager.getInstance();

        // General possible errors
        String errors = "";

        if (fromNode != null) {
            if (!fromNode.isOccupied()) {
                errors += "No stone is placed on node (id=" + fromNode.getID() + ")!\n";
                if (gameManager.getCurrentPlayer() instanceof Player) {
                    gameManager.updateErrorPrompt("Wähle ein Feld mit einem eigenen Stein.");
                }
            }

            if (fromNode.getStone().getOwner() == GameManager.getInstance().getOpponent()) {
                errors += "The stone on node (id=" + fromNode.getID()
                        + ") does not belong to the player whose turn it is (" + player.getName() + "!\n";
                if (gameManager.getCurrentPlayer() instanceof Player) {
                    gameManager.updateErrorPrompt("Du kannst nur deine eigenen Steine bewegen.");
                }
            }

        }
        if (fromNode == toNode) {
            errors += "You can not place a stone onto itself!\n";
            if (gameManager.getCurrentPlayer() instanceof Player) {
                gameManager.updateErrorPrompt("Du kannst keinen Stein auf dasselbe Feld setzen.");
            }
        }

        if (fromNode != toNode && toNode.isOccupied()) {
            errors += "You can not place a stone onto another stone!\n";
            if (gameManager.getCurrentPlayer() instanceof Player) {
                gameManager.updateErrorPrompt("Dieses Feld ist bereits besetzt.");
            }

        }

        // Errors specific to phases
        GamePhase currentPhase = player.getPhase();

        if (currentPhase instanceof SettingPhase) {
            // More errors might come up.
        } else if (currentPhase instanceof MovingPhase && fromNode != null) {
            if (!fromNode.getNeighbors().contains(toNode)) {
                errors += "Can only move to neighbors! A stone can not be moved from " + fromNode.getID() + " to "
                        + toNode.getID() + "!\n";
                if (gameManager.getCurrentPlayer() instanceof Player) {
                    gameManager.updateErrorPrompt("Du darfst nur auf benachbarte Felder ziehen.");
                }

            }

        } else if (currentPhase instanceof JumpingPhase) {
            // More errors might come up.
        } else if (currentPhase instanceof GameOverPhase) {
            errors += "Game is already over. You must not make moves!";
            if (gameManager.getCurrentPlayer() instanceof Player) {
                gameManager.updateErrorPrompt("Das Spiel ist bereits vorbei.");
            }

        } else {
            errors += "Player is not in a legal phase! Player " + player.getName() + " is in phase "
                    + player.getPhase().getClass().getSimpleName() + "\n";
            if (gameManager.getCurrentPlayer() instanceof Player) {
                gameManager.updateErrorPrompt("Ungültige Spielphase für " + player.getName() + ".");
            }

        }

        // No errors occured
        if (errors.isEmpty())
            return true;

        if (player instanceof Player) {
            System.out.print("[RULE] - ");
            System.out.print(errors);
        }
        return false;
    }

    public boolean isRemovalValid(PlayerEntity remover, Node targetNode) {
        boolean removalValid = false;
        Stone targetStone;
        PlayerEntity opponent;

        // Only remove stone, if the targetNode is valid
        if (targetNode.isOccupied() && targetNode.getStone().getOwner() != remover) {
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
        BoardManager boardManager = BoardManager.getInstance();
        PlayerEntity player = lastPlacedStone.getOwner();

        player.printCopy();

        int placedPos = lastPlacedStone.getNode().getID();
        int millCount = 0;

        for (int[] mill : boardManager.potentialMills) {
            // Extract potential mills that contain the position of the newly placed stone
            boolean includesPlaced = false;
            for (int pos : mill) {
                if (pos == placedPos) {
                    includesPlaced = true;
                    break;
                }
            }
            if (!includesPlaced)
                continue;

            // Check if all members of the potential mill are possessed by the current
            // player
            List<Integer> millStones = new ArrayList<>();
            int ownershipCounter = 0;
            for (int pos : mill) {

                Stone stone = boardManager.getNode(pos).getStone();
                if (stone != null && stone.getOwner() == player) {
                    ownershipCounter++;
                    millStones.add(pos);
                }

                for (Stone playerStone : player.getRemainingStones()) {
                    if (playerStone.getNode().getID() == pos && !millStones.contains(pos)) {
                        ownershipCounter++;
                        millStones.add((Integer) playerStone.getNode().getID());
                    }
                }
            }
            // Add one formed mill to the counter
            if (ownershipCounter == 3) {
                //System.out.println("[DEBUG] Mill formed on nodes: " + millStones);
                millCount++;
                if (millCount > 2) {
                    throw new IllegalStateException("More than 2 mills formed — invalid state.");
                }
            }
        }

        return millCount;

    }

    public int countMillsFormed(Node[] move, PlayerEntity player) {
        Node fromNode = move[0];
        Node toNode = move[1];
        Stone stone = null;
        if (fromNode != null) {
            stone = BoardManager.getInstance().getNode(fromNode.getID()).getStone();
        } else {
            stone = new Stone(player);
            player.addStone(stone);
        }
        stone.setNode(BoardManager.getInstance().getNode(toNode.getID()));

        int formedMills = countMillsFormed(stone);

        if (fromNode != null) {
            stone.setNode(BoardManager.getInstance().getNode(fromNode.getID()));
        } else {
            player.removeStone(stone);
        }

        return formedMills;
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
        GameManager gameManager = GameManager.getInstance();

        // Player black won
        if (isPlayerGameOver(playerWhite) || isPlayerBlocked(playerWhite)) {
            System.out.println("\n-- BLACK HAS WON! --");
            gameManager.setWinner(playerBlack);
            return true;
        }
        // Player white won
        if (isPlayerGameOver(playerBlack) || isPlayerBlocked(playerBlack)) {
            System.out.println("\n-- WHITE HAS WON! --");
            gameManager.setWinner(playerWhite);
            return true;
        }

        // Draw
        if (isStalemate()) {
            System.out.println("\n-- DRAW --");
            gameManager.setWinner(null);
            return true;
        }

        return false;
    }

    // region Helpermethods GameOver

    /**
     * Checks if the player is in the game over phase (only 2 stones left).
     * Sets the loss cause to STONECOUNT if so.
     *
     * @param player The player that gets checked.
     * @return true if the player is in game over phase, false otherwise.
     */
    private boolean isPlayerGameOver(PlayerEntity player) {
        if (player.getRemainingStones().size() <= 2 && GameManager.getInstance().getOpponent().allStonesPlaced()) {
            GameManager.getInstance().lossCause = GameManager.LossCause.STONECOUNT;
            player.setPhase(new GameOverPhase(player));
            return true;
        }
        return false;
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
        if (!(player.getPhase() instanceof MovingPhase)) {
            return false;
        }

        // Check for each stone if a neighbor node is occupied
        for (Stone stone : player.getRemainingStones()) {
            for (Node neighbor : stone.getNode().getNeighbors()) {
                if (!(neighbor.isOccupied())) {
                    return false;
                }
            }
        }

        GameManager.getInstance().lossCause = GameManager.LossCause.BLOCKED;
        return true;
    }

    /**
     * Checks how many turns no mill was formed for.
     * Sets the loss cause to STALEMATE if there was no mill for 20 turns.
     *
     * @return true if there was no mill for 20 turns, false otherwise.
     */
    public boolean isStalemate() {
        GameManager gameManager = GameManager.getInstance();
        boolean stalemate = gameManager.getNoMillTurnCounter() >= 20;

        if (stalemate) {
            gameManager.lossCause = GameManager.LossCause.STALEMATE;
        }
        return stalemate;
    }
    // endregion
}