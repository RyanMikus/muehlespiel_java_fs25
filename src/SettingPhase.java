public class SettingPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public SettingPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**
     * In this state the player can place the stones everywhere, if the target Node
     * is not already occupied.
     * Adds a stone and its owner to the players stone list.
     *
     * @param fromNode The origin node (not used here).
     * @param toNode   The target node.
     * @return true if the move was successful, false otherwise.
     */
    @Override
    public boolean handleMove(Node fromNode, Node toNode) {
        if (toNode.isOccupied()) { // Potentially done in isMoveValid()!
            System.out.println("The node is occupied!");
            return false;
        }

        Stone stone = new Stone(playerEntity);
        playerEntity.addStone(stone);

        if (BoardManager.getInstance().placeStone(playerEntity, stone, toNode)) {
            playerEntity.stonePlaced();
            nextPhase();
            return true;
        }

        return false;
    }

    /**
     * Checks if the condition to change the phase is met (all stones are placed).
     * Changes to the moving phase if so.
     */
    @Override
    public void nextPhase() {
        if (playerEntity.allStonesPlaced()) {
            playerEntity.setPhase(new MovingPhase(playerEntity));
        }
    }
}
