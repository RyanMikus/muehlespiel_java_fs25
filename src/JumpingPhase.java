public class JumpingPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public JumpingPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**
     * In this phase the player can jump everywhere, if the target Node is not
     * already occupied.
     *
     * @param fromNode The origin node.
     * @param toNode   The target node.
     * @return true if the move was successful, false otherwise.
     */
    @Override
    public boolean handleMove(Node fromNode, Node toNode) {
        return BoardManager.getInstance().moveStone(playerEntity, fromNode, toNode);
    }

    /**
     * Checks if the condition to change the phase is met (2 stones remaining).
     * Changes to the gameover phase if so.
     */
    @Override
    public void nextPhase() {
        if (playerEntity.getRemainingStones().size() <= 2) {
            playerEntity.setPhase(new GameOverPhase(playerEntity));
            // debug
            System.out.println("Spieler : " + playerEntity.getName() + " wechselt Phase zu : "
                    + playerEntity.getPhase().getClass().getSimpleName());
        }
    }

    /**
     * Creates a deep copy of this JumpingPhase with a new player context.
     *
     * @param newOwner The copied player to whom this phase should belong.
     * @return A new instance of JumpingPhase assigned to the specified player.
     */
    @Override
    public GamePhase deepCopy(PlayerEntity newOwner) {
        return new JumpingPhase(newOwner);
    }

}
