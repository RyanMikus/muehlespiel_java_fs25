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
    public boolean handleMove(Stone stone, Node fromNode, Node toNode) {
        // Potentially done in isMoveValid()
        if(!fromNode.isOccupied() || !(stone.getOwner() == playerEntity)) {
            System.out.println("Invalid origin node!");
            return false;
        }
        else if(toNode.isOccupied()) {
            System.out.println("The node is occupied!");
            return false;
        }
        else if(!fromNode.getNeighbors().contains(toNode)) {
            System.out.println("Can only move to neighbors!");
            return false;
        }

        return BoardManager.getInstance().moveStone(playerEntity, stone, fromNode, toNode);
    }

    /**
     * Checks if the condition to change the phase is met (3 stones remaining).
     * Changes to the jumping phase if so.
     */
    @Override
    public void nextPhase() {
        if(playerEntity.getRemainingStones().size() == 3) {
            playerEntity.setPhase(new JumpingPhase(playerEntity));
        }
    }
}
