import java.util.HashMap;
import java.util.Map;

public class BoardManager extends Observable {
    //region Fields
    private static BoardManager instance;
    private final RuleManager ruleManager;
    private final Map<Integer, Node> nodes;
    private final int[][] neighbors = {{1, 9}, {0, 2, 4}, {1, 14}, {4, 10}, {1, 3, 5, 7}, {4, 13}, {7, 11}, {4, 6, 8}, {7, 12},
            {0, 10, 21}, {3, 9, 11, 18}, {6, 10, 15}, {8, 13, 17}, {5, 12, 14, 20}, {2, 13, 23}, {11, 16}, {15, 17, 19},
            {12, 16}, {10, 19}, {16, 18, 20, 22}, {13, 19}, {9, 22}, {19, 21, 23}, {14, 22}};
    //endregion

    /**
     * Initializes the board
     * <pre>
     * 0 --------- 1 --------- 2
     * |           |           |
     * |   3 ----- 4 ----- 5   |
     * |   |       |       |   |
     * |   |   6 - 7 - 8   |   |
     * |   |   |       |   |   |
     * 9 - 10- 11      12- 13- 14
     * |   |   |       |   |   |
     * |   |   15- 16- 17  |   |
     * |   |       |       |   |
     * |   18----- 19----- 20  |
     * |           |           |
     * 21--------- 22--------- 23
     * </pre>
     */
    private BoardManager() {
        ruleManager = RuleManager.getInstance();
        nodes = new HashMap<>();

        initializeBoard();
    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static BoardManager getInstance() {
        if (instance == null) {
            instance = new BoardManager();
        }
        return instance;
    }

    /**
     * Creates 24 nodes and adds them to the dictionary. Assigns each node an ID.
     * Adds all connections between the nodes.
     */
    public void initializeBoard() {
        // Adding all nodes to the dictionary
        for (int i = 0; i < 24; i++) {
            nodes.put(i, new Node(i));
        }

        // Connecting all nodes
        for(int node = 0; node < neighbors.length; node++) {
            for(int neighbor : neighbors[node]) {
                // Prevent duplicates
                if(node < neighbor) {
                    connectNodes(node, neighbor);
                }
            }
        }

        /*
        for(Node node : nodes.values()) {
            System.out.println("\n" + node.getID() + ":");

            for(Node neighbor : node.getNeighbors()) {
                System.out.println(neighbor.getID());
            }
        }*/
    }

    /**
     * Adds the connections between nodes by adding each other to their neighbors.
     *
     * @param id1 Unique ID of the first node that shall be connected.
     * @param id2 Unique ID of the second node that shall be connected.
     */
    private void connectNodes(int id1, int id2) {
        // Getting the nodes by their ID
        Node node1 = nodes.get(id1);
        Node node2 = nodes.get(id2);

        // Adding each other to their neighbors
        if(node1 != null && node2 != null) {
            node1.addNeighbor(node2);
            node2.addNeighbor(node1);
        }
    }

    /**
     * Checks if the performed move is valid and places the stone on the target node if so.
     * Checks if the placed stone forms a new mill.
     *
     * @param player The player performing the move.
     * @param stone The stone that should be placed.
     * @param toNode The target node.
     * @return true if the performed move is valid, false otherwise.
     */
    public boolean placeStone(PlayerEntity player, Stone stone, Node toNode) {
        if(!ruleManager.isMoveValid()) {
            return false;
        }

        toNode.placeStone(stone);
        notifyObservers();

        if(ruleManager.isMillFormed()) {
            //removeStone();
        }

        return true;
    }

    /**
     * Checks if the performed move is valid and moves the stone to the target if so.
     * Checks if the moved stone forms a mill.
     *
     * @param player The player performing the move
     * @param fromNode The origin node.
     * @param toNode The target node.
     * @return true if the performed move is valid, false otherwise.
     */
    public boolean moveStone(PlayerEntity player, Node fromNode, Node toNode) {
        if(!ruleManager.isMoveValid()) {
            return false;
        }

        Stone stone = fromNode.getStone();
        toNode.placeStone(stone);
        fromNode.removeStone();
        notifyObservers();

        if(ruleManager.isMillFormed()) {
            //removeStone();
            notifyObservers();
        }

        return true;
    }

    /**/
    public void removeStone(Stone targetStone) {
        //targetStone.getOwner().getPhase().nextPhase();
        notifyObservers();
    }

    /**
     * Getter for a specified node.
     * @param id The id of the node that should be returned.
     * @return Node according to ID.
     */
    public Node getNode(int id) {
        return nodes.get(id);
    }
}
