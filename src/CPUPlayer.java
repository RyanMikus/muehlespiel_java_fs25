import java.util.ArrayList;
import java.util.List;

public class CPUPlayer extends PlayerEntity {
    private DifficultyStrategy strategy;
    private PlayerEntity opponent;
    private RuleManager ruleManager;
    private byte lookAhead;

    public CPUPlayer(String name, PlayerEntity opponent, RuleManager ruleManager) {
        super(name);
        this.opponent = opponent;
        this.ruleManager = ruleManager;
    }

    @Override
    public Node makeMove() {
        CPUGameState state = CPUGameStateAdapter.fromPlayers(this, opponent);
        buildGameGraph(state, lookAhead);
        Node[] bestMove = calculateBestMove();
        return (bestMove != null && bestMove.length == 2) ? bestMove[1] : null;
    }

    // TODO: Do we want to get rid of strategy?
    // TODO: Game balancing with lookAhead.
    public void setDifficulty(DifficultyStrategy strategy) {
        this.strategy = strategy;
        if (strategy instanceof EasyDifficulty)
            lookAhead = 1;
        if (strategy instanceof MediumDifficulty)
            lookAhead = 2;
        if (strategy instanceof HardDifficulty)
            lookAhead = 3;
    }

    private List<Node[]> getAllPossibleMoves(PlayerEntity player) {
        // TODO: Can we do this without PlayerEntity? isMoveValid has to use
        // PlayerEntity!
        List<Node[]> possibleMoves = new ArrayList<>();

        if (getPhase() instanceof SettingPhase) {
            for (int i = 0; i <= 23; i++) {
                Node node = new Node(i);
                if (ruleManager.isMoveValid(player, null, node)) {
                    possibleMoves.add(new Node[] { null, node });
                }
            }
        } else if (getPhase() instanceof MovingPhase) {
            for (Stone stone : player.getRemainingStones()) {
                for (Node neighbor : stone.getNode().getNeighbors()) {
                    if (ruleManager.isMoveValid(player, stone.getNode(), neighbor)) {
                        possibleMoves.add(new Node[] { stone.getNode(), neighbor });
                    }
                }
            }
        } else if (getPhase() instanceof JumpingPhase) {
            for (Stone stone : player.getRemainingStones()) {
                for (int i = 0; i <= 23; i++) {
                    Node node = new Node(i);
                    if (ruleManager.isMoveValid(player, stone.getNode(), node)) {
                        possibleMoves.add(new Node[] { stone.getNode(), node });
                    }
                }
            }
        }

        return possibleMoves;
    }

    // recursive method
    private void buildGameGraph(CPUGameState state, byte stepsAhead) {
        // List<Node[]> possibleMoves = getAllPossibleMoves();
        // for (new Node[] move : possibleMoves) {}
        // ...
        // stepsAhead--;
        // for (CPUGameState : possibleGameStates) buildGameGraph(state, stepsAhead);
    }

    private Node[] calculateBestMove() {
        return null;
    }
}
