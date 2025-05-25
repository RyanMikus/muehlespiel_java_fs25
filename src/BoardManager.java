import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardManager extends Observable {
    // region Fields
    private static BoardManager instance;
    private Map<Integer, Node> nodes;
    private final int[][] neighbors = { { 1, 9 }, { 0, 2, 4 }, { 1, 14 }, { 4, 10 }, { 1, 3, 5, 7 }, { 4, 13 },
            { 7, 11 }, { 4, 6, 8 }, { 7, 12 },
            { 0, 10, 21 }, { 3, 9, 11, 18 }, { 6, 10, 15 }, { 8, 13, 17 }, { 5, 12, 14, 20 }, { 2, 13, 23 }, { 11, 16 },
            { 15, 17, 19 },
            { 12, 16 }, { 10, 19 }, { 16, 18, 20, 22 }, { 13, 19 }, { 9, 22 }, { 19, 21, 23 }, { 14, 22 } };
    public final List<int[]> potentialMills;
    // endregion

    // region Initialization
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
                    connectNodes(nodes, node, neighbor);
                }
            }
        }
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
    private void connectNodes(Map<Integer, Node> board, int id1, int id2) {
        // Getting the nodes by their ID
        Node node1 = board.get(id1);
        Node node2 = board.get(id2);

        // Adding each other to their neighbors
        if (node1 != null && node2 != null) {
            node1.addNeighbor(node2);
            node2.addNeighbor(node1);
        }
    }
    // endregion

    // region Events
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
        RuleManager ruleManager = RuleManager.getInstance();

        // Check if the stone can be placed correctly
        if (!ruleManager.isMoveValid(player, stone.getNode(), toNode)) {
            //System.out.println("[DEBUG] -> Move ungültig laut Regelwerk.");
            return false;
        }

        // Place stone and update GUI
        toNode.placeStone(stone);
        //System.out.println("[DEBUG] → Stein platziert auf Node " + toNode.getID());
        //System.out.println("placeStone ---- updateGUI");
        notifyObservers();

//        System.out.println("[" + (player.getName().equalsIgnoreCase("white") ? "WHITE" : "BLACK") +
//                "] - Placed stone on node " + toNode.getID());
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
        if (RuleManager.getInstance().isRemovalValid(remover, targetNode)) {
            Stone targetStone = targetNode.getStone();
            PlayerEntity opponent = targetStone.getOwner();
            targetNode.removeStone();
            opponent.removeStone(targetStone);
            opponent.getPhase().nextPhase();
            stoneRemoved = true;
            System.out.println("[" + (remover.getName().equalsIgnoreCase("white") ? "WHITE" : "BLACK") +
                    "] - Removed stone on node " + targetNode.getID());
        }
        return stoneRemoved;
    }

    /**
     * Checks if the performed move is valid and moves the stone to the target if
     * so.
     * Checks if the moved stone forms zero, one or two mill.
     *
     * @param player   The player performing the move.
     * @param fromNode The origin node.
     * @param toNode   The target node.
     * @return true if the performed move is valid, false otherwise.
     */
    public boolean moveStone(PlayerEntity player, Node fromNode, Node toNode) {
        RuleManager ruleManager = RuleManager.getInstance();

        // Check if the stone can be moved correctly
        if (!ruleManager.isMoveValid(player, fromNode, toNode)) {
            return false;
        }

        // Move the stone and update GUI
        Stone stone = fromNode.getStone();
        toNode.placeStone(stone);
        fromNode.removeStone();
        //System.out.println("[" + (player.getName().equalsIgnoreCase("white") ? "WHITE" : "BLACK") +                "] - Move stone from " + fromNode.getID() + " to " + toNode.getID());

        //System.out.println("moveStone ---- updateGUI");
        notifyObservers();

        return true;
    }
    // endregion

    // region Setters / Getters
    /**
     * Getter for the current board.
     *
     * @return The Map all nodes are stored in.
     */
    public Map<Integer, Node> getBoard() {
        return nodes;
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

    /**
     * Sets the board to a new one, updating all nodes and their parameters.
     *
     * @param newBoard The new Hashmap, containing all nodes of a board.
     */
    public void setBoard(Map<Integer, Node> newBoard, PlayerEntity currentPlayer, PlayerEntity opponent) {
        nodes.clear();

        // 1. Jeden Knoten aus der Map holen, ID vom Original und Node als Kopie in neue Map legen
        for (Map.Entry<Integer, Node> entry : newBoard.entrySet()) {
            Node originalNode = entry.getValue();
            Node copyNode = originalNode.deepCopy(currentPlayer, opponent);
            nodes.put(entry.getKey(), copyNode);
        }
        // Nach dieser Schleife haben wir eine neue Map mit 24 kopierten Knoten. Die Kopien
        // besitzen ebenso kopierte Steine wenn welche vorhanden waren. Der kopierte Knoten
        // wurde auf den kopierten Stein gesetzt und umgekehrt.
        // Die LinkedList mit den Nachbarn ist bei allen Kopien aktuell noch LEER!

        // 2. Knoten wieder verbinden
        for (int node = 0; node < neighbors.length; node++) {
            for (int neighbor : neighbors[node]) {
                // Prevent duplicates
                if (node < neighbor) {
                    connectNodes(nodes, node, neighbor);
                }
            }
        }
    }
    // endregion

    /**
     * Printing each Node and the stone it is currently holding.
     * If there is no stone the owner is "-", else the name of the owner is printed.
     */
    public void printBoard(Map<Integer, Node> board) {
        System.out.println("-- BOARD -- ");
        for (Node node : board.values()) {
            if (node.getStone() != null){
                String owner = (node.getStone() == null) ? "-" : node.getStone().getOwner().getName();
                String line = String.format("ID: %2d, Owner: %s", node.getID(), owner);
                System.out.println(line);
            }

        }

    }

    /**
     * Creates a deep copy of the current board, including all nodes, stones,
     * and their neighbor relationships.
     *
     * @param currentPlayer The copied current player.
     * @param opponent      The copied opponent player.
     * @return A fully deep-copied board as a map of node IDs to Node instances.
     */
    public Map<Integer, Node> copyBoard(PlayerEntity currentPlayer, PlayerEntity opponent) {
        Map<Integer, Node> copiedBoard = new HashMap<>();

        // 1. Jeden Knoten aus der Map holen, ID vom Original und Node als Kopie in neue Map legen
        for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
            Node originalNode = entry.getValue();
            Node copyNode = originalNode.deepCopy(currentPlayer, opponent);
            copiedBoard.put(entry.getKey(), copyNode);
        }

        // 2. Knoten wieder verbinden
        for (int node = 0; node < neighbors.length; node++) {
            for (int neighbor : neighbors[node]) {
                // Prevent duplicates
                if (node < neighbor) {
                    connectNodes(copiedBoard, node, neighbor);
                }
            }
        }
        return copiedBoard;
    }

    public void reset() {
        nodes.clear();
        initializeBoard();
        //System.out.println("reset ---- updateGUI");
        notifyObservers();
    }
}
