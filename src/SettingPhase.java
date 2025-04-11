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
     * In this state the player can place the stones everywhere, if the target Node is not already occupied.
     *
     * @param stone The stone being placed.
     * @param fromNode The origin Node (not used here).
     * @param toNode The target Node
     * @return true if the move was successful, false otherwise
     */
    @Override
    public boolean handleMove(Stone stone, Node fromNode, Node toNode) {
        if(toNode.isOccupied()) {   // Potentially done in isMoveValid()!
            System.out.println("The node is occupied");
            return false;
        }

        if(BoardManager.getInstance().placeStone(playerEntity, stone, toNode)) {
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
        if(playerEntity.allStonesPlaced()) {
            playerEntity.setPhase(new MovingPhase(playerEntity));
        }
    }
}
