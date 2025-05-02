public class MovingPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public MovingPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**/
    @Override
    public boolean handleMove(Node fromNode, Node toNode) {
        return BoardManager.getInstance().moveStone(playerEntity, fromNode, toNode);
    }

    /**
     * Checks if the condition to change the phase is met (3 stones remaining).
     * Changes to the jumping phase if so.
     */
    @Override
    public void nextPhase() {
        if (playerEntity.getRemainingStones().size() == 3) {
            playerEntity.setPhase(new JumpingPhase(playerEntity));
            // debug
            System.out.println("Spieler : " + playerEntity.getName() + " wechselt Phase zu : "
                    + playerEntity.getPhase().getClass().getSimpleName());
        }
    }

    /**
     * Creates a deep copy of this MovingPhase with a new player context.
     *
     * @param newOwner The copied player to whom this phase should belong.
     * @return A new instance of MovingPhase assigned to the specified player.
     */
    @Override
    public GamePhase deepCopy(PlayerEntity newOwner) {
        return new MovingPhase(newOwner);
    }

}
