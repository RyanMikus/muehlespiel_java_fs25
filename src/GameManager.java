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
        NONE,
        STONECOUNT,
        BLOCKED,
        STALEMATE,
        TIMEOUT
    }

    public LossCause lossCause = LossCause.NONE;

    private PlayerEntity winner;
    private int noMillTurnCounter = 0;
    private int timePerTurn;
    private TurnTimer timer;
    // endregion

    //region Initialization

    /**
     * Initialize managers
     */
    private GameManager() {
        boardManager = BoardManager.getInstance();
        ruleManager = RuleManager.getInstance();
        guiManager = new GUI();

        boardManager.setRuleManager(ruleManager);
        ruleManager.setBoardManager(boardManager);
        ruleManager.setGameManager(this);

        history = new History();
    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static GameManager getInstance() {
        if(instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    //endregion

    //region Gameflow

    /**/
    public void startGame() {
        // Initialize players
        // TODO: Second player can be CPU or another player depending on playing Singleplayer or Multiplayer
        playerWhite = new Player("White");
        playerBlack = new CPUPlayer("Black");
        currentPlayer = playerWhite;

        // Adding the initial state of the game to the history, allowing to undo until the very start of the game.
        Move initialMove = new Move(playerWhite, playerBlack, boardManager.getBoard(), 0, timePerTurn);
        history.addMove(initialMove);

        startTurnTimer();
    }

    /**
     * Switches the current player, that can perform moves.
     * Resets and starts the timer again for the next player.
     */
    public void switchPlayer() {
        currentPlayer = getOpponent();
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
        boolean moveSuccessful = player.getPhase().handleMove(fromNode, toNode);

        if(!moveSuccessful) {
            return;
        }

        noMillTurnCounter++;
        if(ruleManager.isGameOver(playerWhite, playerBlack)) {
            gameOver();
            return;
        }

        saveMove();
        switchPlayer();
    }

    /**
     * Gets called when a loss condition gets fulfilled.
     * Leads to the Game over screen with the winner as a parameter.
     */
    public void gameOver() {
        guiManager.showGameOverScreen(winner);
    }

    //endregion

    //region Setters / Getters

    /**
     * Setter for the player that has won the game.
     *
     * @param winner The player that has won.
     */
    public void setWinner(PlayerEntity winner) {
        this.winner = winner;
    }

    /**
     * Getter for the player that has won the game.
     * Can be Black, White or a null (Draw).
     *
     * @return PlayerBlack or PlayerWhite if there is a winner, null otherwise.
     */
    public PlayerEntity getWinner() {
        return winner;
    }

    /**
     * Getter for the player that is not currently able to perform a move.
     * Helper method for switching players.
     *
     * @return playerWhite if the current player is playerBlack and vice versa.
     */
    public PlayerEntity getOpponent() {
        if(currentPlayer == playerWhite) {
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
     * Resets the amount of turns that no mill was formed to 0.
     */
    public void resetNoMillTurnCounter() {
        noMillTurnCounter = 0;
    }

    /**
     * Getter for the time a player has to perform a move before he loses the game.
     *
     * @return The currently set time a player has for his move.
     */
    public int getTimePerTurn() {
        return timePerTurn;
    }

    /**
     * Setter for the time a player has to perform a move before he loses the game.
     *
     * @param timePerTurn The new time a player shall have to perform a move.
     */
    public void setTimePerTurn(int timePerTurn) {
        this.timePerTurn = timePerTurn;
    }
    //endregion

    //region Timer

    /**
     * Starts a new timer with the set time per turn.
     * Updates the timer label on every tick (time per tick set in the timer class).
     * When the time runs out, the game is over and the current player loses.
     */
    public void startTurnTimer() {
        timer = new TurnTimer(timePerTurn,
                () -> guiManager.updateTimer(timer.getTimeLeft()),  // Happens each Tick
                () -> {                                             // Happens when the time runs out
                    winner = getOpponent();
                    lossCause = LossCause.TIMEOUT;
                    guiManager.showTimeOverMsg(currentPlayer);
                    gameOver();
                }
        );

        timer.startTimer();
    }

    /**
     * Stops the timer, if it is initialized correctly.
     */
    public void stopTurnTimer() {
        if(timer != null) {
            timer.stopTimer();
        }
    }

    //endregion

    //region Save / Restore

    /**
     * Adds the current state of the game to the History (Memento).
     * This includes both players, the current state of the board, the number of moves no mill was formed
     * and the time left for the move.
     */
    public void saveMove() {
        Move move = new Move(currentPlayer, getOpponent(), boardManager.getBoard(), noMillTurnCounter, timer.getTimeLeft());
        history.addMove(move);
    }

    /**
     * Undoes a move, restoring the state before the last performed move, if there is a undo move.
     *
     * @see #loadMove(Move)
     */
    public void undoMove() {
        Move lastMove = history.undoMove();
        if(lastMove != null) {
            loadMove(lastMove);
        }
    }

    /**
     * Redoes a move, restoring the state that was undone prior, if there is a move to redo.
     *
     * @see #loadMove(Move)
     */
    public void redoMove() {
        Move nextMove = history.redoMove();
        if(nextMove != null) {
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
        if(currentPlayer.getName().equalsIgnoreCase("white")) {
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
}
