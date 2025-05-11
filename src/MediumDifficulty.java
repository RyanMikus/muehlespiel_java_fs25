import java.util.List;
import java.util.Random;

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
        List<Node[]> playerMoves = player.getAllPossibleMoves();
        List<Node[]> opponentMoves = opponent.getAllPossibleMoves();
        // Try to make a mill
        for (Node[] playerMove : playerMoves) {
            if (RuleManager.getInstance().countMillsFormed(playerMove[1], player) >= 1) {
                return playerMove;
            }
            if (playerMove[1].getNeighbors().contains(playerMove[1])) {
                return playerMove;
            }
        }
        // Block the opponents mill from forming.
        for (Node[] opponentMove : opponentMoves) {
            if (RuleManager.getInstance().countMillsFormed(opponentMove[1], opponent) >= 1) {
                System.out.println("saas");
                int targetNodeID = opponentMove[1].getID();
                for (Node[] playerMove : playerMoves) {
                    if (playerMove[1].getID() == targetNodeID)
                        return playerMove;
                }
            }
        }
        // TODO: Try to place a stone to set up a potential mill
        Random random = new Random();
        return playerMoves.get(random.nextInt(playerMoves.size()));
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        // TODO: make it smarter
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        for (Node node : removals) {
            List<Node> neighbors = node.getNeighbors();
            for (Node neighborNode : neighbors) {
                if (neighborNode.isOccupied() && neighborNode.getStone().getOwner() == opponent) {
                    return node;
                }
            }
        }
        Random random = new Random();
        return removals.get(random.nextInt(removals.size()));
    }
}
