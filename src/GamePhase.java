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

    public abstract boolean handleMove(Stone stone, Node fromNode, Node toNode);
    public abstract void nextPhase();
}
