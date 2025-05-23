import java.util.List;

public class HardDifficulty extends DifficultyStrategy {

    /**/
    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node[]> playerMoves = player.getAllPossibleMoves();
        List<Node[]> opponentMoves = opponent.getAllPossibleMoves();
        Node[] decidedMove = null;

        decidedMove = findMill(playerMoves, player);
        if (decidedMove != null) {
            return decidedMove;
        }

        decidedMove = blockMill(playerMoves, opponentMoves, opponent);
        if (decidedMove != null) {
            return decidedMove;
        }

        decidedMove = clusterMove(playerMoves, player);
        if (decidedMove != null) {
            return decidedMove;
        }

        return randomMove(playerMoves);
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        List<Node[]> opponentMoves = opponent.getAllPossibleMoves();

        Node removalNode = highValueRemoval(removals, opponentMoves, opponent);
        if (removalNode != null) {
            return removalNode;
        }

        return randomRemoval(removals);
    }
}