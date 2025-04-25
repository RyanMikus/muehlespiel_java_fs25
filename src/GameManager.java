public class GameManager {
    // region Fields
    // Managers
    private static GameManager instance;
    private BoardManager boardManager;
    private RuleManager ruleManager;
    private final GUI guiManager;

    // Players
    private PlayerEntity playerWhite = new Player("white");
    private PlayerEntity playerBlack = new Player("black");
    private PlayerEntity currentPlayer;

    public enum LossCause {
        NONE,
        STONECOUNT,
        BLOCKED,
        STALEMATE,
        TIMEOUT
    }
    public LossCause lossCause = LossCause.NONE;

    private boolean gameStatus; // true when game is running

    private PlayerEntity winner;
    private int noMillTurnCounter = 0;
    private int timePerTurn;
    private TurnTimer timer;
    // endregion

    /**
     * Initialize managers
     */
    private GameManager() {
        boardManager = BoardManager.getInstance();
        ruleManager = RuleManager.getInstance();
        guiManager = GUI.getInstance();

        boardManager.setRuleManager(ruleManager);
        boardManager.setGuiManager(guiManager);
        boardManager.addObserver(guiManager);

        ruleManager.setBoardManager(boardManager);
        ruleManager.setGameManager(this);
        guiManager.initializeManagers(this, boardManager);

        currentPlayer = playerWhite;
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

    /**/
    public void startGame() {
        gameStatus = true;
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
     * Gets called when a loss condition gets fulfilled.
     * Leads to the Game over screen with the winner as a parameter.
     */
    public void gameOver() {
        guiManager.showGameOverScreen(winner);
    }

    //region Setters and Getters
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
     * Getter for the player that is currently able to perform a move.
     * 
     * @return playerWhite or playerBlack
     */

    public PlayerEntity getCurrentPlayer(){
        return currentPlayer;
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
        }
        else {
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

    /**
     * Delegates the move done by a player to the current phase the player is in.
     * Only switches the player, if the move was successful.
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

        
        if (ruleManager.isGameOver(playerWhite, playerBlack)) {
            gameOver();
            return;
        }
        
        switchPlayer();
    }

    /**
     * Starts a new timer with the set time per turn.
     * Updates the timer label on every tick (time per tick set in the timer class).
     * When the time runs out, the game is over and the current player loses.
     */
    public void startTurnTimer() {
        timer = new TurnTimer(guiManager.getTimePerTurn(),
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

    /**/
    public void undoMove() {

    }

    /**/
    public void redoMove() {

    }

    /**/
    public void saveGame() {

    }

    /**/
    public void loadGame() {

    }

    public RuleManager getRuleManager() {
        return ruleManager;
    }

    public BoardManager getBoardManager(){
        return boardManager;
    }

    public boolean getGameStatus(){
        return gameStatus;
    }
}
