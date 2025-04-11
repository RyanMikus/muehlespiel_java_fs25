public class CPUPlayer extends PlayerEntity {
    private DifficultyStrategy strategy;

    public CPUPlayer(String name) {
        super(name);
    }

    /**/
    @Override
    public Node makeMove() {
        return null;
    }

    /**/
    public void setDifficulty(DifficultyStrategy strategy) {
        this.strategy = strategy;
    }
}
