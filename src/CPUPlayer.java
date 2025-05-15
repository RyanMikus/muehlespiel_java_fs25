public class CPUPlayer extends PlayerEntity {
    private DifficultyStrategy strategy;
    private PlayerEntity opponent;

    public CPUPlayer(String name, PlayerEntity opponent) {
        super(name);
        this.opponent = opponent;
        this.strategy = new MediumDifficulty();
    }

    public Node[] makeMove() {
        RuleManager ruleManager = RuleManager.getInstance();
        ImaginativePlayer imaginativeSelf = new ImaginativePlayer(this, ruleManager);
        ImaginativePlayer imaginativeOpponent = new ImaginativePlayer(opponent, ruleManager);
        // strategy.buildGameTree(imaginativeSelf, imaginativeOpponent);
        return strategy.calculateMove(imaginativeSelf, imaginativeOpponent);
    }

    public Node makeRemove() {
        RuleManager ruleManager = RuleManager.getInstance();
        ImaginativePlayer imaginativeSelf = new ImaginativePlayer(this, ruleManager);
        ImaginativePlayer imaginativeOpponent = new ImaginativePlayer(opponent, ruleManager);
        return strategy.calculateRemoval(imaginativeSelf, imaginativeOpponent);
    }

    public void setDifficulty(DifficultyStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public PlayerEntity deepCopy() {
        return null;
    }
}