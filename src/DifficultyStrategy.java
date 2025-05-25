import java.util.List;
import java.util.Random;

public abstract class DifficultyStrategy {

    /**/
    abstract Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent);

    abstract Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent);

    protected Node[] randomMove(List<Node[]> moves) {
        Random random = new Random();
        return moves.get(random.nextInt(moves.size()));
    }

    protected Node[] findMill(List<Node[]> moves, ImaginativePlayer player) {
        for (Node[] playerMove : moves) {
            if (RuleManager.getInstance().countMillsFormed(playerMove, player) >= 1) {
                return playerMove;
            }
        }
        return null;
    }

    protected Node[] blockMill(List<Node[]> playerMoves, List<Node[]> opponentMoves, ImaginativePlayer opponent) {
        for (Node[] opponentMove : opponentMoves) {
            if (RuleManager.getInstance().countMillsFormed(opponentMove, opponent) >= 1) {
                int targetNodeID = opponentMove[1].getID();
                for (Node[] playerMove : playerMoves) {
                    if (playerMove[1].getID() == targetNodeID) {
                        return playerMove;
                    }
                }
            }
        }
        return null;
    }

    protected Node[] clusterMove(List<Node[]> playerMoves, ImaginativePlayer player) {
        // Find a Node in playerMoves which has a stone of the same player and another
        // unoccupied node as a neighbor.
        for (Node[] playerMove : playerMoves) {
            Node targetNode = playerMove[1];
            for (Node nodeNeighbor : targetNode.getNeighbors()) {
                if (!nodeNeighbor.isOccupied()) {
                    int unoccupiedNeighborCount = 0;
                    int stoneNeighbor = 0;
                    for (Node nodeNeighborNeighbor : nodeNeighbor.getNeighbors()) {
                        if (!nodeNeighborNeighbor.isOccupied()) {
                            unoccupiedNeighborCount++;
                        } else if (nodeNeighborNeighbor.isOccupied()) {
                            for (Stone stone : player.getRemainingStones()) {
                                if (stone.getNode().getID() == nodeNeighborNeighbor.getID())
                                    stoneNeighbor++;
                            }
                        }
                    }
                    if (stoneNeighbor >= 2 && unoccupiedNeighborCount >= 1) {
                        //System.out.println("Real magic does exist");
                        for (Node[] move : playerMoves) {
                            if (move[1].getID() == nodeNeighbor.getID())
                                return move;
                        }
                    }
                }
            }
        }
        return null;
    }

    protected Node randomRemoval(List<Node> removals) {
        Random random = new Random();
        return removals.get(random.nextInt(removals.size()));
    }

    protected Node highValueRemoval(List<Node> removals, List<Node[]> opponentMoves, ImaginativePlayer opponent) {
        // Check every possible removal how many mills could be formed with it on that
        // position. Return the Node with the highest value.
        for (Node[] move : opponentMoves) {
            int potMillCount = RuleManager.getInstance().countMillsFormed(move, opponent);
            if (potMillCount >= 1) {
                for (Node node : removals) {
                    if (move[0] != null && move[0].getID() == node.getID()) {
                        return move[0];
                    }
                }
            }
        }
        return null;
    }
}
