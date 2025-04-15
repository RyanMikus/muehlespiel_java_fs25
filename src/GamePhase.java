public abstract class GamePhase {
    protected PlayerEntity playerEntity;

    /**
     * Context gets passed through the constructor.
     *
     * @param playerEntity Context that uses the phase.
     */
    public GamePhase(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public boolean handleMove(Node fromNode, Node toNode) {
        return false;
    }

    public void nextPhase() {
    }
}
