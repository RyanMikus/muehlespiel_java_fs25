import java.util.List;
import java.util.Random;

public class EasyDifficulty implements DifficultyStrategy {
    public final byte lookAhead = 1;
    private TreeNode root; // remembers the tree

    @Override
    public void buildGameTree(ImaginativePlayer imaginativeCPUPlayer, ImaginativePlayer imaginativeOpponent) {
        GameTree gameTree = new GameTree();
        root = gameTree.buildGameTree(imaginativeCPUPlayer, imaginativeOpponent, lookAhead);
    }

    /**/
    @Override
    public Node[] calculateMove(PlayerEntity player, PlayerEntity opponent) {
        if (root == null || root.getChildren().isEmpty()) {
            System.out.println("Game tree is empty!");
            return new Node[0]; // Kein Zug möglich
        }
        Random random = new Random();
        List<TreeNode> children = root.getChildren();
        TreeNode randomChild = children.get(random.nextInt(children.size()));
        return randomChild.getMove();
    }
}