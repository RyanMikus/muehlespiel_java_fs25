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
        if (!fromNode.isOccupied()) {
            System.out.println("Invalid origin node!");
            return false;
        } else if (fromNode.getStone().getOwner() != playerEntity) {
            System.out.println("Stone is not owned by the player!");
            return false;
        } else if (toNode.isOccupied()) { // Potentially done in isMoveValid()!
            System.out.println("The node is occupied!");
            return false;
        }

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
        }
    }
}
