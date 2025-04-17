public class Stone {
    private PlayerEntity owner;
    private Node currentNode;

    public Stone(PlayerEntity owner) {
        this.owner = owner;
    }

    /**/
    public void moveToNode(Node node) {

    }

    /**
     * Sets the node the stone is placed on.
     *
     * @param node The target node.
     */
    public void setNode(Node node) {
        currentNode = node;
    }

    /**
     * Getter for the node the stone currently is placed on.
     *
     * @return The current node.
     */
    public Node getNode() {
        return currentNode;
    }

    /**
     * Getter for the owner of the stone.
     *
     * @return The owner of the stone.
     */
    public PlayerEntity getOwner() {
        return owner;
    }
}
