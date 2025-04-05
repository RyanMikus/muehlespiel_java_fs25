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
    private static GameManager getInstance() {
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

    /**/
    public void switchPlayer() {

    }

    /**/
    public PlayerEntity getWinner() {
        return null;
    }

    /**/
    public void handleMove() {

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
