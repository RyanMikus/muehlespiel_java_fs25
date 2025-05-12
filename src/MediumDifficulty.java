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

        // Find a Node in playerMoves which has a stone of the same player and another
        // unoccupied node as a neighbor.
        for (Node[] playerMove : playerMoves) {
            Node targetNode = playerMove[1];
            for (Node nodeNeighbor : targetNode.getNeighbors()) {
                if (!nodeNeighbor.isOccupied()) {
                    int unoccupiedNeighborCount = 0;
                    boolean stoneNeighbor = false;
                    for (Node nodeNeighborNeighbor : nodeNeighbor.getNeighbors()) {
                        if (!nodeNeighborNeighbor.isOccupied()) {
                            unoccupiedNeighborCount++;
                        } else if (nodeNeighborNeighbor.isOccupied()) {
                            for (Stone stone : player.getRemainingStones()) {
                                if (stone.getNode().getID() == nodeNeighborNeighbor.getID()) {
                                    stoneNeighbor = true;
                                }
                            }
                        }
                    }
                    if (stoneNeighbor && unoccupiedNeighborCount >= 3) {
                        System.out.println("Real magic does exist");
                        for (Node[] move : playerMoves) {
                            if (move[1].getID() == nodeNeighbor.getID())
                                return move;
                        }
                    }
                }
            }
        }

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
