import java.util.List;

/**
 * EasyDifficulty is a strategy class that represents a simple difficulty level
 * for an imaginative player in a game. It provides basic implementations for
 * calculating moves and removals by selecting random options from the available
 * possibilities. This strategy is designed to offer a low-challenge gameplay
 * experience.
 */
public class EasyDifficulty extends DifficultyStrategy {

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