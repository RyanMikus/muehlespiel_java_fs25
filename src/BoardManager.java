import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardManager extends Observable {
    // region Fields
    private static BoardManager instance;
    private final RuleManager ruleManager;
    private final Map<Integer, Node> nodes;
    private final int[][] neighbors = { { 1, 9 }, { 0, 2, 4 }, { 1, 14 }, { 4, 10 }, { 1, 3, 5, 7 }, { 4, 13 },
            { 7, 11 }, { 4, 6, 8 }, { 7, 12 },
            { 0, 10, 21 }, { 3, 9, 11, 18 }, { 6, 10, 15 }, { 8, 13, 17 }, { 5, 12, 14, 20 }, { 2, 13, 23 }, { 11, 16 },
            { 15, 17, 19 },
            { 12, 16 }, { 10, 19 }, { 16, 18, 20, 22 }, { 13, 19 }, { 9, 22 }, { 19, 21, 23 }, { 14, 22 } };
    public final List<int[]> potentialMills;
    // endregion

    /**
     * Initializes the board
     * 
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
        potentialMills = new ArrayList<>();

        initializeBoard();
        initializeMills();
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
        for (int node = 0; node < neighbors.length; node++) {
            for (int neighbor : neighbors[node]) {
                // Prevent duplicates
                if (node < neighbor) {
                    connectNodes(node, neighbor);
                }
            }
        }

        /*
         * for(Node node : nodes.values()) {
         * System.out.println("\n" + node.getID() + ":");
         * 
         * for(Node neighbor : node.getNeighbors()) {
         * System.out.println(neighbor.getID());
         * }
         * }
         */
    }

    /**
     * Creates all potential mills as int[] with the integers being the nodes id.
     * Adds all potential mills into a public array.
     */
    private void initializeMills() {
        // Horizontal mills
        potentialMills.add(new int[] { 0, 1, 2 });
        potentialMills.add(new int[] { 3, 4, 5 });
        potentialMills.add(new int[] { 6, 7, 8 });
        potentialMills.add(new int[] { 9, 10, 11 });
        potentialMills.add(new int[] { 12, 13, 14 });
        potentialMills.add(new int[] { 15, 16, 17 });
        potentialMills.add(new int[] { 18, 19, 20 });
        potentialMills.add(new int[] { 21, 22, 23 });

        // Vertical mills
        potentialMills.add(new int[] { 0, 9, 21 });
        potentialMills.add(new int[] { 3, 10, 18 });
        potentialMills.add(new int[] { 6, 11, 15 });
        potentialMills.add(new int[] { 1, 4, 7 });
        potentialMills.add(new int[] { 16, 19, 22 });
        potentialMills.add(new int[] { 8, 12, 17 });
        potentialMills.add(new int[] { 5, 13, 20 });
        potentialMills.add(new int[] { 2, 14, 23 });
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
        if (node1 != null && node2 != null) {
            node1.addNeighbor(node2);
            node2.addNeighbor(node1);
        }
    }

    /**
     * Checks if the performed move is valid and places the stone on the target node
     * if so.
     * Checks if the placed stone forms a new mill.
     *
     * @param player The player performing the move.
     * @param stone  The stone that should be placed.
     * @param toNode The target node.
     * @return true if the performed move is valid, false otherwise.
     */
    public boolean placeStone(PlayerEntity player, Stone stone, Node toNode) {
        if (!ruleManager.isMoveValid(player, stone.getNode(), toNode)) {
            return false;
        }

        toNode.placeStone(stone);
        notifyObservers();

        int millCount = ruleManager.countMillsFormed(stone);
        while (millCount > 0) {
            System.out.println("Remove an oponents stone.");
            notifyObservers(); // Has to be a blocking function
            millCount--;
        }

        return true;
    }

    /**
     * Checks if a stone can be removed, then removes it and sets the next GamePhase
     * if needed.
     * 
     * @param remover    The player removing a stone.
     * @param targetNode The node chosen by the remover to remove a stone from.
     * @return true if stone got removed, false if removal was invalid.
     */
    public boolean removeStone(PlayerEntity remover, Node targetNode) {
        boolean stoneRemoved = false;
        Stone targetStone;
        PlayerEntity opponent;
        if (targetNode.isOccupied() && targetNode.getStone().getOwner() != remover) {
            targetStone = targetNode.getStone();
            opponent = targetStone.getOwner();
            boolean freeStones = false; // Does the opponent have stones which are not in a mill?
            for (Stone stone : opponent.getRemainingStones()) {
                if (ruleManager.countMillsFormed(stone) == 0)
                    freeStones = true;
            }
            if (ruleManager.countMillsFormed(targetStone) == 0 || !freeStones) {
                opponent.removeStone(targetStone);
                targetNode.removeStone();
                stoneRemoved = true;
            }
            if (stoneRemoved && opponent.getRemainingStones().size() == 3)
                opponent.setPhase(new JumpingPhase(opponent));
            // The following logic might want to be implemented in another method. Reason
            // for this being that if the opponent has 3 stones remaining, the opponent
            // should automatically lose without a player needing to take away 1 or 2
            // stones. This could be implemented in the method placeStone. If so, placeStone
            // would need access to both players.
            if (opponent.getRemainingStones().size() <= 2) {
                opponent.setPhase(new GameOverPhase(opponent));
                notifyObservers(); // oponent lost, no more stones need to be removed
            }
        }
        return stoneRemoved;
    }

    /**
     * Checks if the performed move is valid and moves the stone to the target if
     * so.
     * Checks if the moved stone forms zero, one or two mill.
     *
     * @param player   The player performing the move
     * @param stone    The stone that should be placed.
     * @param player   The player performing the move
     * @param fromNode The origin node.
     * @param toNode   The target node.
     * @return true if the performed move is valid, false otherwise.
     */
    public boolean moveStone(PlayerEntity player, Node fromNode, Node toNode) {
        if (!ruleManager.isMoveValid(player, fromNode, toNode)) {
            return false;
        }

        Stone stone = fromNode.getStone();
        toNode.placeStone(stone);
        fromNode.removeStone();
        notifyObservers();

        int millCount = ruleManager.countMillsFormed(stone);
        while (millCount > 0) {
            // removeStone();
            notifyObservers();
            millCount--;
        }

        return true;
    }

    /**
     * Getter for a specified node.
     * 
     * @param id The id of the node that should be returned.
     * @return Node according to ID.
     */
    public Node getNode(int id) {
        return nodes.get(id);
    }
}
