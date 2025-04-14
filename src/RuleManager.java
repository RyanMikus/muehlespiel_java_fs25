import java.util.ArrayList;
import java.util.List;

public class RuleManager {
    private static RuleManager instance;
    private static BoardManager boardManager;

    private RuleManager() {
        boardManager = BoardManager.getInstance();
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

    /**/
    public boolean isMoveValid() {
        return false;
    }

    /*
     * Only gets called once, when a move is made, otherwise multiple mills would be
     * formed.
     */
    public int countNewMillsFormed(Stone lastPlacedStone) {
        PlayerEntity player = lastPlacedStone.getOwner();
        // Create a mutable list to store neighboring nodes occupied by the same owner
        List<Node> ownersOccupiedNeighbors = new ArrayList<>();

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
                    throw new RuntimeException("More than 2 mills formed — invalid state.");
                }
            }
        }

        return millCount;
    }

    /**/
    public boolean isGameOver() {
        return false;
    }

    /**/
    public boolean isPlayerBlocked() {
        return false;
    }

    /**/
    public boolean isTimeOver() {
        return false;
    }

    /**/
    public boolean isStalemate() {
        return false;
    }
}
