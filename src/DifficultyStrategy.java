public interface DifficultyStrategy {

    /**/
    void buildGameTree(ImaginativePlayer imaginativeCPUPlayer, ImaginativePlayer imaginativeOpponent);

    /**/
    Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent);

    Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent);
}
