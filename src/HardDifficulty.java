public class HardDifficulty implements DifficultyStrategy {
    private final byte lookAhead = 2;

    @Override
    public void buildGameTree(ImaginativePlayer imaginativeCPUPlayer, ImaginativePlayer imaginativeOpponent) {
        GameTree gameTree = new GameTree();
        gameTree.buildGameTree(imaginativeCPUPlayer, imaginativeOpponent, lookAhead);
    }

    /**/
    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        return null;
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        return null;
    }
}