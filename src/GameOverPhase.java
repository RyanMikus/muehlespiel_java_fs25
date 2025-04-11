public class GameOverPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public GameOverPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**/
    @Override
    public boolean handleMove(Stone stone, Node fromNode, Node toNode) {
        return false;
    }

    /**/
    @Override
    public void nextPhase() {

    }
}
