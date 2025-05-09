import java.awt.Color;
import java.security.PKCS12Attribute;

public class GameManager {
    // region Fields
    // Managers
    private static GameManager instance;
    private final BoardManager boardManager;
    private final RuleManager ruleManager;
    private final GUI guiManager;
    private final History history;

    // Players
    private PlayerEntity playerWhite;
    private PlayerEntity playerBlack;
    private PlayerEntity currentPlayer;

    public enum LossCause {
        NONE, STONECOUNT, BLOCKED, STALEMATE, TIMEOUT
    }

    public LossCause lossCause = LossCause.NONE;

    private boolean gameStatus; // true when game is running
    private volatile Node[] playerMove; // Store current move input
    private volatile Node playerRemove; // Store removal move input

    private PlayerEntity winner;
    private int noMillTurnCounter = 0;
    private int timePerTurn = 30;
    private TurnTimer timer;
    // endregion

    // region Initialization

    /**
     * Initialize managers
     */
    private GameManager() {
        boardManager = BoardManager.getInstance();
        ruleManager = RuleManager.getInstance();
        guiManager = GUI.getInstance();

        history = new History();
    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Registers a listener in the GUI that receives completed player moves.
     * <p>
     * When a move is received, it stores it and wakes up the waiting game loop.
     */
    public void initializeListeners(GUIBoard guiBoard) {
        guiBoard.setMoveListener(move -> {
            this.playerMove = move;
            synchronized (this) {
                this.notify();
            }
        });

        guiBoard.setRemoveListener(move -> {
            this.playerRemove = move;
            synchronized (this) {
                this.notify();
            }
        });
    }

    // endregion

    // region Gameflow

    /**
     * Gets called when the startbutton gets pressed and initializes the game in a
     * separate thread.
     * <p>
     * Creates players according to chosen mode and sets the gameStatus to true,
     * indicating the game is running.
     * <p>
     * Adds an initial move to the history allowing to undo until the very start of
     * the game.
     *
     * @param mode Can either be Singleplayer (Player and CPU) or Multiplayer (Two
     *             players).
     */
    public void startGame(String mode) {
        System.out.format("-- Starting game in mode: %s --\n\n", mode);
        // Starting Singleplayer mode with a Player and a CPUPlayer
        if (mode.equalsIgnoreCase("singleplayer")) {
            playerWhite = new Player("white");
            playerBlack = new CPUPlayer("black", playerWhite);
            gameStatus = true;
            currentPlayer = playerWhite;
            startTurnTimer();
        }
        // Starting Multiplayer mode with 2 Players
        else if (mode.equalsIgnoreCase("multiplayer")) {
            playerWhite = new Player("white");
            playerBlack = new Player("black");
            gameStatus = true;
            currentPlayer = playerWhite;
            startTurnTimer();
        } else {
            System.out.println("[ERROR] - Invalid mode!");
        }

        // Adding the initial state of the game to the history, allowing to undo until
        // the very start of the game.
        Move initialMove = new Move(playerWhite, playerBlack, boardManager.getBoard(), 0, timePerTurn);
        history.addMove(initialMove);

        new Thread(this::runGame).start();
    }

    /**
     * Controls the game flow by waiting for the user's input or cpu's calculations
     * and then performing the move
     * according to it. Similarly waits for a choice and performs it to remove a
     * stone when a mill is formed.
     * <p>
     * Checks if the game is over and saves the changes in the history if not.
     * 
     * @see #getMoveInput(boolean)
     * @see #getRemoveInput(boolean)
     */
    public void runGame() {
        Node[] move;
        Node remove;

        while (gameStatus) {
            boolean isPlayer = currentPlayer instanceof Player;

            // update game prompt
            updateGamePrompt();

            // Handle moves of players
            move = getMoveInput(isPlayer);
            Node fromNode = move[0];
            Node toNode = move[1];

            // Perform move according to choices of players
            handleMove(currentPlayer, fromNode, toNode);
            boardManager.notifyObservers();
            noMillTurnCounter++;

            // Handle removal of stones
            int millCount = ruleManager.countMillsFormed(toNode.getStone());
            if (millCount > 0) {
                System.out.println("[" + (currentPlayer.getName().equalsIgnoreCase("white")
                        ? "WHITE"
                        : "BLACK") + "] - Formed " + millCount + " " + ((millCount == 1) ? "mill" : "mills"));
            }
            while (millCount > 0) {
                updateRemovePrompt();
                noMillTurnCounter = 0;
                guiManager.getGuiBoard().setRemoveMode(true);
                remove = getRemoveInput(isPlayer);

                // Perform remove according to choices of players
                if (!boardManager.removeStone(currentPlayer, remove)) {
                    continue;
                }
                guiManager.getGuiBoard().setRemoveMode(false);
                boardManager.notifyObservers();
                millCount--;
            }

            if (ruleManager.isGameOver(playerWhite, playerBlack)) {
                gameOver();
            }

            saveMove();
            switchPlayer();
        }
    }

    /**
     * Helpermethod for the runGame-Method.
     * <p>
     * Waits for a valid player input (via drag & drop) or performs the CPU move.
     *
     * @param isPlayer true if the current player is human, false if it's a
     *                 CPUPlayer.
     * @return A Node array [fromNode, toNode], or null if interrupted
     * @see #runGame()
     */
    private Node[] getMoveInput(boolean isPlayer) {
        if (isPlayer) {
            playerMove = null;

            synchronized (this) {
                while (playerMove == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
            return playerMove;
        } else {
            return ((CPUPlayer) currentPlayer).makeMove();
        }
    }

    /**
     * Helpermethod for the runGame-Method.
     * <p>
     * Waits for the selection of an opponent's stone to remove, or lets the CPU
     * automatically choose a stone to remove.
     *
     * @param isPlayer true if the current player is human
     * @return The Node selected for removal, or null if interrupted
     * @see #runGame()
     */
    private Node getRemoveInput(boolean isPlayer) {
        if (isPlayer) {
            playerRemove = null;

            synchronized (this) {
                while (playerRemove == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
            return playerRemove;
        } else {
            return ((CPUPlayer) currentPlayer).makeRemove();
        }
    }

    /**
     *
     */
    public void resetGame(String mode) {
        // TODO: this method Silvio
        gameStatus = false;
        startGame(mode);
    }

    /**
     * Switches the current player, that can perform moves.
     * Resets and starts the timer again for the next player.
     */
    public void switchPlayer() {
        currentPlayer = getOpponent();

        if (currentPlayer.getName().equalsIgnoreCase("white")) {
            System.out.println();
        }

        guiManager.updateTimer(timePerTurn);
        timer.resetTimer();
    }

    /**
     * Delegates the move done by a player to the current phase the player is in.
     * Saves the last move and switches the player, if the move was successful.
     *
     * @param player   The player making a move.
     * @param fromNode The origin node.
     * @param toNode   The target node.
     */
    public void handleMove(PlayerEntity player, Node fromNode, Node toNode) {
        player.getPhase().handleMove(fromNode, toNode);
    }

    /**
     * Gets called when a loss condition gets fulfilled.
     * Leads to the Game over screen with the winner as a parameter.
     */
    public void gameOver() {
        stopTurnTimer();
        gameStatus = false;
        guiManager.showGameOverMsg(winner, getOpponent());
        System.out.println("-- Ending Game --");
    }
    // endregion

    // region Setters / Getters

    /**
     * Setter for the player that has won the game.
     *
     * @param winner The player that has won.
     */
    public void setWinner(PlayerEntity winner) {
        this.winner = winner;
    }

    /**
     * Getter for the player that is currently able to perform a move.
     *
     * @return playerWhite or playerBlack
     */
    public PlayerEntity getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Getter for the player that is not currently able to perform a move.
     * Helper method for switching players.
     *
     * @return playerWhite if the current player is playerBlack and vice versa.
     */
    public PlayerEntity getOpponent() {
        if (currentPlayer == playerWhite) {
            return playerBlack;
        } else {
            return playerWhite;
        }
    }

    /**
     * Getter for the amount of turns that no mill was formed.
     *
     * @return The value of the noMillTurnCounter.
     */
    public int getNoMillTurnCounter() {
        return noMillTurnCounter;
    }

    /**
     * Setter for the time a player has to perform a move before he loses the game.
     *
     * @param timePerTurn The new time a player shall have to perform a move.
     */
    public void setTimePerTurn(int timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    /**
     * Getter for the ruleManager.
     *
     * @return The RuleManager-Instance.
     */
    public RuleManager getRuleManager() {
        return ruleManager;
    }

    /**
     * Getter for the current status of the game.
     *
     * @return true if the game is running, false otherwise.
     */
    public boolean getGameStatus() {
        return gameStatus;
    }

    // endregion

    // region Timer

    /**
     * Starts a new timer with the set time per turn.
     * Updates the timer label on every tick (time per tick set in the timer class).
     * When the time runs out, the game is over and the current player loses.
     */
    public void startTurnTimer() {
        timer = new TurnTimer(timePerTurn, () -> guiManager.updateTimer(timer.getTimeLeft()), // Happens each Tick
                () -> { // Happens when the time runs out
                    lossCause = LossCause.TIMEOUT;
                    guiManager.showGameOverMsg(getOpponent(), currentPlayer);
                    gameOver();
                });

        timer.startTimer();
    }

    /**
     * Stops the timer, if it is initialized correctly.
     */
    public void stopTurnTimer() {
        if (timer != null) {
            timer.stopTimer();
        }
    }
    // endregion

    // region Save / Restore

    /**
     * Adds the current state of the game to the History (Memento).
     * This includes both players, the current state of the board, the number of
     * moves no mill was formed
     * and the time left for the move.
     */
    public void saveMove() {
        Move move = new Move(currentPlayer, getOpponent(), boardManager.getBoard(), noMillTurnCounter,
                timer.getTimeLeft());
        history.addMove(move);
    }

    /**
     * Undoes a move, restoring the state before the last performed move, if there
     * is a undo move.
     *
     * @see #loadMove(Move)
     */
    public void undoMove() {
        Move lastMove = history.undoMove();
        if (lastMove != null) {
            loadMove(lastMove);
        }
    }

    /**
     * Redoes a move, restoring the state that was undone prior, if there is a move
     * to redo.
     *
     * @see #loadMove(Move)
     */
    public void redoMove() {
        Move nextMove = history.redoMove();
        if (nextMove != null) {
            loadMove(nextMove);
        }
    }

    /**
     * Helper-Method for undoing or redoing a move.
     * Resets all Parameters that got changed after performing a move.
     *
     * @param move The move that should be loaded. Can be an undo or redo move.
     * @see #undoMove()
     * @see #redoMove()
     */
    private void loadMove(Move move) {
        // Restore players and set the current player
        currentPlayer = move.getCurrentPlayer();
        if (currentPlayer.getName().equalsIgnoreCase("white")) {
            playerWhite = currentPlayer;
            playerBlack = move.getOpponent();
        } else {
            playerBlack = currentPlayer;
            playerWhite = move.getOpponent();
        }

        // Restore the single parameters that get updated each move.
        boardManager.setBoard(move.getBoardSnapshot());
        noMillTurnCounter = move.getNoMillTurnCounter();
        timer.setTimeLeft(move.getTimeLeft());
    }

    /**/
    public void saveGame() {

    }

    /**/
    public void loadGame() {

    }

    // endregion

    // region GUI Update
    public void updateGamePrompt() {
        String text = "";

        // player text
        if (currentPlayer instanceof Player) {
            if (currentPlayer.getName().equalsIgnoreCase("white")) {
                text += "Weiss: ";
            } else {
                text += "Schwarz: ";
            }
        }

        switch (currentPlayer.getPhase().getClass().getSimpleName()) {
            case "SettingPhase":
                text += "Stein setzen";
                break;

            case "MovingPhase":
                text += "Stein ziehen";
                break;

            case "JumpingPhase":
                text += "Stein bewegen (Sprung erlaubt)";
                break;

            case "GameOverPhase":

                break;

            default:
                break;
        }
        guiManager.updateStatus(text, new Color(255, 230, 0));
    }


    public void updateErrorPrompt(String text){
        guiManager.updateStatus(text, new Color(255, 0, 0));
    }

    public void updateRemovePrompt(){
        String text = "";

        // player text
        if (currentPlayer instanceof Player) {
            if (currentPlayer.getName().equalsIgnoreCase("white")) {
                text = "Mühle Weiss : Stein entfernen";
            } else {
                text = "Mühle Schwarz : Stein entfernen";
            }
        }

        guiManager.updateStatus(text, new Color(0, 153, 255));
    }

    // enregion
}