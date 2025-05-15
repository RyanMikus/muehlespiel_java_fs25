import java.util.List;
import java.util.Random;

public class EasyDifficulty implements DifficultyStrategy {
    public final byte lookAhead = 1;

    @Override
    public void buildGameTree(ImaginativePlayer imaginativeCPUPlayer, ImaginativePlayer imaginativeOpponent) {
        return;
    }

    /**/
    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Node[]> moves = player.getAllPossibleMoves();
        Random random = new Random();
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        Random random = new Random();
        return removals.get(random.nextInt(removals.size()));
    }
}