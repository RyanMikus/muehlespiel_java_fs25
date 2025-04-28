public class MediumDifficulty implements DifficultyStrategy {
    private final byte lookAhead = 2;

    @Override
    public void buildGameTree(ImaginativePlayer imaginativeCPUPlayer, ImaginativePlayer imaginativeOpponent) {
        GameTree gameTree = new GameTree();
        gameTree.buildGameTree(imaginativeCPUPlayer, imaginativeOpponent, lookAhead);
    }

    /**/
    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        // if (root == null || root.getChildren().isEmpty()) {
        // System.out.println("Game tree is empty!");
        // return new Node[0]; // Kein Zug möglich
        // }
        // Random random = new Random();
        // List<TreeNode> children = root.getChildren();
        // TreeNode randomChild = children.get(random.nextInt(children.size()));
        // return randomChild.getMove();
        return null;
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        return null;
    }
}
