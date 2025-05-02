import java.util.Map;

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
    private PlayerEntity CPUPlayer;
    private PlayerEntity currentPlayer;
    private PlayerEntity opponent;

    // Copys
    private PlayerEntity currentPlayerCopy;
    private PlayerEntity opponentCopy;
    private Map<Integer, Node> boardCopy;

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

    // region Initialization

    /**
     * Initialize managers
     */
    private GameManager() {
        boardManager = BoardManager.getInstance();
        ruleManager = RuleManager.getInstance();
        guiManager = GUI.getInstance();

        boardManager.setRuleManager(ruleManager);
        boardManager.setGuiManager(guiManager);
        ruleManager.setBoardManager(boardManager);
        ruleManager.setGameManager(this);
        guiManager.initializeManagers(this, boardManager);

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

    // endregion

    // region Gameflow

    /**/
    public void startGame(String mode) {

        if (mode.equalsIgnoreCase("singleplayer")) {
            playerWhite = new Player("white");
            // CPUPlayer = new CPUPlayer("black");
            gameStatus = true;
            currentPlayer = playerWhite;
            startTurnTimer();
        } else if (mode.equalsIgnoreCase("multiplayer")) {
            playerWhite = new Player("white");
            playerBlack = new Player("black");
            gameStatus = true;
            currentPlayer = playerWhite;
            startTurnTimer();
        } else {
            System.out.println("Error 404... Spiel konnte nicht gestartet werden");
        }

        // Adding the initial state of the game to the history, allowing to undo until
        // the very start of the game.
        Move initialMove = new Move(
                playerWhite.deepCopy(),
                playerBlack.deepCopy(),
                boardManager.copyBoard(playerWhite.deepCopy(), playerBlack.deepCopy()),
                0,
                guiManager.getTimePerTurn());
        history.addMove(initialMove);

    }

    /**
     *
     */
    public void resetGame(String mode) {
        gameStatus = false;
        startGame(mode);
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
        if (player instanceof CPUPlayer) {
            Node[] move = ((CPUPlayer) player).makeMove();
            fromNode = move[0];
            toNode = move[1];
        }
    
        System.out.println("[DEBUG] handleMove: Spieler = " + player.getName() + ", Ziel-Node = " + toNode.getID());
    
        boolean moveSuccessful = player.getPhase().handleMove(fromNode, toNode);
    
        if (!moveSuccessful) {
            System.out.println("[DEBUG] → Zug fehlgeschlagen – kein Speichern");
            return;
        }
    
        noMillTurnCounter++;
        if (ruleManager.isGameOver(playerWhite, playerBlack)) {
            gameOver();
            return;
        }
    
        saveMove(); // ✅ Zug war erfolgreich – Zustand sichern
        switchPlayer();
    }

    /**
     * Gets called when a loss condition gets fulfilled.
     * Leads to the Game over screen with the winner as a parameter.
     */
    public void gameOver() {
        stopTurnTimer();
        gameStatus = false;
        String strLossCause = lossCause.name(); // TODO - Maybe there is a better Solution for that
        System.out.println(strLossCause);
        guiManager.showGameOverMsg(winner, strLossCause);
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
        System.out.println("Gewinner : " + winner.getName());
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
     * Increments the amount of turns no mill was formed by 1.
     */
    public void incNoMillTurnCounter() {
        noMillTurnCounter++;
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

    /**
     * Getter for the boardManager.
     * 
     * @return The BoardManager-Instance
     */
    public BoardManager getBoardManager() {
        return boardManager;
    }

    // endregion

    // region Timer
    /**
     * Starts a new timer with the set time per turn.
     * Updates the timer label on every tick (time per tick set in the timer class).
     * When the time runs out, the game is over and the current player loses.
     */
    public void startTurnTimer() {
        timer = new TurnTimer(guiManager.getTimePerTurn(),
                () -> guiManager.updateTimer(timer.getTimeLeft()), // Happens each Tick
                () -> { // Happens when the time runs out
                    winner = getOpponent();
                    lossCause = LossCause.TIMEOUT;
                    String strLossCause = lossCause.name(); // TODO - Maybe there is a better Solution for that
                    guiManager.showGameOverMsg(winner, strLossCause);
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
        currentPlayerCopy = currentPlayer.deepCopy();
        opponentCopy = getOpponent().deepCopy();
        boardCopy = boardManager.copyBoard(currentPlayerCopy, opponentCopy);

        Move move = new Move(opponentCopy, currentPlayerCopy, boardCopy, noMillTurnCounter, timer.getTimeLeft());
        
        Move lastMove = history.peekUndo();
    if (lastMove == null || !move.equals(lastMove)) {
        history.addMove(move);
        System.out.println("[DEBUG] Neuer Move gespeichert → Spieler: " + currentPlayerCopy.getName());
    } else {
        System.out.println("[DEBUG] Neuer Move identisch → nicht gespeichert");
    }
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
            System.out.println("[DEBUG] RedoMove ausgeführt → Spieler: " + nextMove.getCurrentPlayer().getName());
            loadMove(nextMove);
        } else {
            System.out.println("[INFO] Redo nicht möglich – Stack leer");
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
    public void loadMove(Move move) {

        if (move == null) {
            System.out.println("[FATAL] loadMove wurde mit null aufgerufen – verbotener Pfad!");
            new Exception().printStackTrace(); // <-- zeigt dir die Stelle
            return;
        }

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

        boardManager.notifyObservers();

        System.out.println("[DEBUG] loadMove → Spieler: " + currentPlayer.getName());
    }

    /**/
    public void saveGame() {

    }

    /**/
    public void loadGame() {

    }

    // for UndoRedotest
    public void setCurrentPlayer(PlayerEntity player) {
        currentPlayer = player;
    }

    public void setOpponent(PlayerEntity player) {
        opponent = player;
    }

    public void setTurnTimer(TurnTimer timer2) {
        timer = timer2;
    }

    // endregion
}