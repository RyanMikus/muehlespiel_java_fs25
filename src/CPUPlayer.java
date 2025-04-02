public class CPUPlayer extends PlayerEntity {
    private DifficultyStrategy strategy;

    /**/
    @Override
    public void makeMove() {

    }

    /**/
    public void setDifficulty(DifficultyStrategy strategy) {
        this.strategy = strategy;
    }
}
