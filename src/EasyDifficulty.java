import java.util.List;

public class EasyDifficulty extends DifficultyStrategy {
    public final byte lookAhead = 1;

    /**/
    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node[]> moves = player.getAllPossibleMoves();
        return randomMove(moves);
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        return randomRemoval(removals);
    }
}