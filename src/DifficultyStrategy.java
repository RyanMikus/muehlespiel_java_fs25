public interface DifficultyStrategy {

    /**/
    void buildGameTree(ImaginativePlayer imaginativeCPUPlayer, ImaginativePlayer imaginativeOpponent);

    /**/
    Node[] calculateMove(PlayerEntity player, PlayerEntity opponent);
}
