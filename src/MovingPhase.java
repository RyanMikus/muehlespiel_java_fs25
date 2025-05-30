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

            System.out.println("[" + (playerEntity.getName().equalsIgnoreCase("white")
                    ? "WHITE" : "BLACK") + "] - Changed to JUMPINGPHASE");
        }
    }
}
