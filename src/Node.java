import java.util.LinkedList;
import java.util.List;

public class Node {
    //region Fields
    private final int id;
    private Stone stone;
    private final List<Node> neighbors;
    //endregion

    /**
     * Constructs a node with the given ID.
     * Initializes the node with no stone and an empty list of neighbors.
     *
     * @param id The unique identifier of the node.
     */
    public Node(int id) {
        this.id = id;
        this.stone = null;
        this.neighbors = new LinkedList<>();
    }

    /**
     * Checks whether this node contains a stone or not.
     *
     * @return true if a stone is placed on this node, false otherwise.
     */
    public boolean isOccupied() {
        return stone == null;
    }

    /**
     * Adds a given node to the list of neighbors.
     *
     * @param node The node that gets added to the neighbors.
     */
    public void addNeighbor(Node node) {
        neighbors.add(node);
    }

    /**
     * Getter for the list containing all neighbor nodes.
     *
     * @return List containing all neighbor nodes
     */
    public List<Node> getNeighbors() {
        return neighbors;
    }

    /**
     * Assigns the given stone to this node. Can only possess one stone at a time.
     *
     * @param stone The stone that gets assigned to this node.
     * @throws IllegalStateException if the node is already occupied.
     */
    public void placeStone(Stone stone) {
        if(this.stone != null) {
            throw new IllegalStateException("Node is already occupied");
        }
        this.stone = stone;
    }

    /**
     * Removes the assigned stone from this node.
     *
     * @throws IllegalStateException if the node doesn't contain a stone
     */
    public void removeStone() {
        if(this.stone == null) {
            throw new IllegalStateException("Node is already empty");
        }
        stone = null;
    }

    /**
     * Getter for the node's ID.
     *
     * @return The current ID of the node.
     */
    public int getID() {
        return id;
    }
}
