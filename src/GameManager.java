public class GameManager {
    //region Fields
    // Managers
    private static GameManager instance;
    private final BoardManager boardManager;
    private final GUI guiManager;

    // Players
    private PlayerEntity playerWhite;
    private PlayerEntity playerBlack;
    private PlayerEntity currentPlayer;
    //endregion

    /**
     * Initialize managers
     */
    private GameManager() {
        boardManager = BoardManager.getInstance();
        guiManager = new GUI();
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

    public static void main(String[] args) {
        GameManager manager =  GameManager.getInstance();
    }

    /**/
    public void startGame() {

    }

    /**
     * Switches the current player, that can perform moves.
     */
    public void switchPlayer() {
        if(currentPlayer == playerWhite) {
            currentPlayer = playerBlack;
        }
        else {
            currentPlayer = playerWhite;
        }
    }

    /**/
    public PlayerEntity getWinner() {
        return null;
    }

    /**
     * Delegates the move done by a player to the current phase the player is in.
     * Only switches the player, if the move was successful.
     *
     * @param player The player making a move.
     * @param stone The stone that should be moved.
     * @param fromNode The origin node.
     * @param toNode The target node.
     */
    public void handleMove(PlayerEntity player, Stone stone, Node fromNode, Node toNode) {
        boolean moveSuccessful = player.getPhase().handleMove(stone, fromNode, toNode);

        if(moveSuccessful) {
            switchPlayer();
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
}
