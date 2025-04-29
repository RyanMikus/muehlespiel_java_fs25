public class CPUPlayer extends PlayerEntity {
    private DifficultyStrategy strategy;
    private PlayerEntity opponent;
    private RuleManager ruleManager;

    public CPUPlayer(String name, PlayerEntity opponent, RuleManager ruleManager) {
        super(name);
        this.opponent = opponent;
        this.ruleManager = ruleManager;
    }

    public Node[] makeMove() {
        ImaginativePlayer imaginativeSelf = new ImaginativePlayer(this, ruleManager);
        ImaginativePlayer imaginativeOpponent = new ImaginativePlayer(opponent, ruleManager);
        // strategy.buildGameTree(imaginativeSelf, imaginativeOpponent);
        Node[] move = strategy.calculateMove(imaginativeSelf, imaginativeOpponent);
        return move;
    }

    public void setDifficulty(DifficultyStrategy strategy) {
        this.strategy = strategy;
    }
}