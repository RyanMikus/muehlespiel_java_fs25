import java.util.List;
import java.util.Random;

public class EasyDifficulty implements DifficultyStrategy {
    public final byte lookAhead = 1;
    private TreeNode root; // remembers the tree

    @Override
    public void buildGameTree(ImaginativePlayer imaginativeCPUPlayer, ImaginativePlayer imaginativeOpponent) {
        return;
    }

    /**/
    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node[]> moves = player.getAllPossibleMoves();
        Random random = new Random();
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        Random random = new Random();
        return removals.get(random.nextInt(removals.size()));
    }
}