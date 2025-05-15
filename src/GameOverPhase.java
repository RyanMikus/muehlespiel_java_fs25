public class GameOverPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public GameOverPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**
     * Creates a deep copy of this GameOverPhase with a new player context.
     *
     * @param newOwner The copied player to whom this phase should belong.
     * @return A new instance of GameOverPhase assigned to the specified player.
     */
    @Override
    public GamePhase deepCopy(PlayerEntity newOwner) {
        return new GameOverPhase(newOwner);
    }

}
