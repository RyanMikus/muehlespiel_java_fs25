import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public class GUIBoard extends JPanel{
    // region Fields
    private Integer fromNodeId = null;
    private Node fromNode = null;
    
    private boolean removeMode = false;
    private Consumer<Node[]> moveListener = null;
    private Consumer<Node> removeListener = null;

    private final Map<Integer, Point> nodePositions = new HashMap<>();
    private final int boardSize = 400;
    private int offsetX, offsetY;

    // Drag & Drop
    private Point dragPosition = null;
    private Color draggedColor = null;
    private final int stoneRadius = 15;

    // Steine links/rechts
    private final List<Point> whiteStoneSlots = new ArrayList<>();
    private final List<Point> blackStoneSlots = new ArrayList<>();
    private int whiteStonesLeft = 9;
    private int blackStonesLeft = 9;
    // endregion

    public GUIBoard() {
        GameManager gameManager = GameManager.getInstance();
        BoardManager boardManager = BoardManager.getInstance();

        setPreferredSize(new Dimension(700, 500));
        setOpaque(false);
        initNodePositions();
        initStoneSlots();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (!gameManager.getGameStatus()) {
                    return;
                }

                if(!removeMode){
                    PlayerEntity currentPlayer = gameManager.getCurrentPlayer();

                    // Checks if the white player wants to move his own stone
                    if (currentPlayer.getName().equalsIgnoreCase("white")) {
                        for (int i = 0; i < whiteStonesLeft; i++) {
                            if (whiteStoneSlots.get(i).distance(e.getPoint()) < 15) {
                                System.out.println("Pressed on a white stone.");
                                draggedColor = Color.WHITE;
                                dragPosition = e.getPoint();
                                return;
                            }
                        }
                    }

                    // Checks if the black player wants to move his own stone
                    if (currentPlayer.getName().equalsIgnoreCase("black")) {
                        for (int i = 0; i < blackStonesLeft; i++) {
                            if (blackStoneSlots.get(i).distance(e.getPoint()) < 15) {
                                System.out.println("Pressed on a black stone.");
                                draggedColor = Color.BLACK;
                                dragPosition = e.getPoint();
                                return;
                            }
                        }
                    }

                    // Checks if a node has been clicked (not necessary for the setting phase) and gets it if so
                    for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
                        int guiNodeId = entry.getKey();
                        Point p = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);

                        if (p.distance(e.getPoint()) < 15 && !(currentPlayer.getPhase() instanceof SettingPhase)) {
                            Node node = boardManager.getNode(guiNodeId);

                            // debug
                            //System.out.println("MP - Snapping Node Board -> " + node.getID());

                            // Valid node clicked
                            if (node.isOccupied() && node.getStone().getOwner() == currentPlayer) {
                                fromNodeId = guiNodeId;
                                dragPosition = e.getPoint();
                                draggedColor = node.getStone().getOwner().getName().equals("white") ? Color.WHITE
                                        : Color.BLACK;

                                // debug
                                //System.out.println(" MP - Stone from Gui Node -> " + fromNodeId);

                                return;
                            }
                        }  
                    }
                } else {
                    signalPlayerRemove(e.getPoint());
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (!gameManager.getGameStatus() || draggedColor == null){
                    return;
                }

                // Checks if a stone has been released over a node and gets it if so
                for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
                    int guiNodeId = entry.getKey();
                    Point p = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);
                    
                    if (p.distance(e.getPoint()) < 15) {
                        Node toNode = boardManager.getNode(guiNodeId);
                        
                        // debug
                        //System.out.println("MR - Snapping Node Board -> " + toNode.getID());

                        // Checks if the stone was played from outside the edge of the game board or not
                        if (fromNodeId != null) {
                            fromNode = boardManager.getNode(fromNodeId);
                            
                            // debug
                            //System.out.println(" MR - Stone from Board Node ->" + fromNode.getID());
                        }

                        PlayerEntity currentPlayer = gameManager.getCurrentPlayer();

                        // debug
                        //System.out.println("  MR - Current Player -> " + currentPlayer.getName());

                        // Check if move is valid
                        if (gameManager.getRuleManager().isMoveValid(currentPlayer, fromNode, toNode)) {
                            // debug
                            //System.out.println("   MR - Move was valid");
                            //System.out.println("    MR - Handle Move.. Player Phase -> " + currentPlayer.getPhase().getClass().getSimpleName());

                            signalPlayerMove(fromNode ,toNode);
                            
                            // debug
                            //System.out.println("     MR - Move handled correct.. new Player: " + gameManager.getCurrentPlayer().getName());
                            
                            // Update amount of stones on edge of the game board
                            if (draggedColor == Color.WHITE && whiteStonesLeft > 0){
                                whiteStonesLeft--;
                                
                                // debug
                               //System.out.println("      MR - White Stones left -> " + whiteStonesLeft);
                            }
                            else if (draggedColor == Color.BLACK && blackStonesLeft > 0){
                                blackStonesLeft--;

                                // debug
                                //System.out.println("      MR - Black Stones left -> " + blackStonesLeft);
                            }
                                
                            fromNode = null;
                            toNode = null;
                            break;
                        } 
                    }
                }

                // When player "loses" the stone without snapping into a valid Node
                repaint();

                // Reset der Drag-Infos
                dragPosition = null;
                draggedColor = null;
                }            
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!gameManager.getGameStatus()){
                    return;
                }
                
                if (draggedColor != null) {
                    dragPosition = e.getPoint();
                    repaint();
                }
            }
        });

    }
    // region Initialization
    /**
     * Initialize the Map with 24 Entrys, with a specific position (Point) on the Board 
     * The points are necessary for Mouse listeners
     */
    private void initNodePositions() {
        nodePositions.put(0, new Point(0, 0));
        nodePositions.put(1, new Point(200, 0));
        nodePositions.put(2, new Point(400, 0));
        nodePositions.put(3, new Point(50, 50));
        nodePositions.put(4, new Point(200, 50));
        nodePositions.put(5, new Point(350, 50));
        nodePositions.put(6, new Point(100, 100));
        nodePositions.put(7, new Point(200, 100));
        nodePositions.put(8, new Point(300, 100));
        nodePositions.put(9, new Point(0, 200));
        nodePositions.put(10, new Point(50, 200));
        nodePositions.put(11, new Point(100, 200));
        nodePositions.put(12, new Point(300, 200));
        nodePositions.put(13, new Point(350, 200));
        nodePositions.put(14, new Point(400, 200));
        nodePositions.put(15, new Point(100, 300));
        nodePositions.put(16, new Point(200, 300));
        nodePositions.put(17, new Point(300, 300));
        nodePositions.put(18, new Point(50, 350));
        nodePositions.put(19, new Point(200, 350));
        nodePositions.put(20, new Point(350, 350));
        nodePositions.put(21, new Point(0, 400));
        nodePositions.put(22, new Point(200, 400));
        nodePositions.put(23, new Point(400, 400));
    }

    /**
     * Initialize the Array with 9 Stones, with a specific position (Point) on the Board 
     * The points are necessary for Mouse listeners
     */
    private void initStoneSlots() {
        for (int i = 0; i < 9; i++) {
            whiteStoneSlots.add(new Point(25, 80 + i * 42));
            blackStoneSlots.add(new Point(675, 80 + i * 42));
        }
        System.out.println("Boardgröße bei initStoneSlots: " + getWidth() + " x " + getHeight());
    }
    // endregion

    // region Events

    /**
     * Sets a listener that will be notified when a complete move (from and to node) is selected.
     *
     * @param listener A consumer that accepts a Node array of length 2: [fromNode, toNode].
     */
    public void setMoveListener(Consumer<Node[]> listener) {
        this.moveListener = listener;
    }

    /**
     * Signals that the player has selected a move.
     * This should be called once both mousePressed and mouseReleased have been processed.
     *
     * @param from The node the stone is moved from.
     * @param to The node the stone is moved to.
     */
    public void signalPlayerMove(Node from, Node to) {
        if (moveListener != null) {
            moveListener.accept(new Node[] { from, to });
        } else {
            System.out.println("No moveListener was registered");
        }
    }

    /**
     * Sets a listener that will be notified when a complete move (remove) is selected.
     *
     * @param listener A consumer that accepts a Node to remove its stone.
     */
    public void setRemoveListener(Consumer<Node> listener){
        this.removeListener = listener;
    }

    /**
     * Activates the removeMode that gets used to block all listeners except for the removing listener.
     */
    public void activateRemoveMode() {
        removeMode = true;
    }

    /**
     * When a mill is formed the player choose one stone over the gui
     * 
     * @param targetPoint - The Point(Stone) the Player wants to remove
     */
    public void signalPlayerRemove(Point targetPoint) {
        BoardManager boardManager = BoardManager.getInstance();
        
        // debug
        //System.out.println("RSM - currentRemover -> " + currentRemover.getName());

        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int nodeId = entry.getKey();
            Point guiPoint = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);
    
            if (guiPoint.distance(targetPoint) < 15) {
                Node targetNode = boardManager.getNode(nodeId);

                if(removeListener != null) {
                    removeMode = false;
                    repaint();
                    removeListener.accept(targetNode);
                }

                return;
            }
        }
    }

    /**
     * When GUI-Board is initialized, this method will be called automatically to draw the Lines, Nodes and Stones.
     * Can also be manually called by repaint()
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        offsetX = (getWidth() - boardSize) / 2;
        offsetY = (getHeight() - boardSize) / 2;

        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(offsetX, offsetY, 400, 400);
        g2.drawRect(offsetX + 50, offsetY + 50, 300, 300);
        g2.drawRect(offsetX + 100, offsetY + 100, 200, 200);
        g2.drawLine(offsetX + 200, offsetY, offsetX + 200, offsetY + 100);
        g2.drawLine(offsetX + 200, offsetY + 300, offsetX + 200, offsetY + 400);
        g2.drawLine(offsetX, offsetY + 200, offsetX + 100, offsetY + 200);
        g2.drawLine(offsetX + 300, offsetY + 200, offsetX + 400, offsetY + 200);

        for (Point p : nodePositions.values()) {
            int x = offsetX + p.x;
            int y = offsetY + p.y;
            g2.fillOval(x - 5, y - 5, 10, 10);
        }

        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int id = entry.getKey();
            Point p = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);

            Node node = BoardManager.getInstance().getNode(id);
            if (node != null && node.isOccupied()) {
                Stone stone = node.getStone();
                PlayerEntity owner = stone.getOwner();

                Color color = owner.getName().equalsIgnoreCase("white") ? Color.WHITE : Color.BLACK;

                g2.setColor(color);
                g2.fillOval(p.x - stoneRadius, p.y - stoneRadius, stoneRadius * 2, stoneRadius * 2);
            }
        }

        g2.setColor(Color.WHITE);
        for (int i = 0; i < whiteStonesLeft; i++) {
            Point p = whiteStoneSlots.get(i);
            g2.fillOval(p.x - stoneRadius, p.y - stoneRadius, stoneRadius * 2, stoneRadius * 2);
        }

        g2.setColor(Color.BLACK);
        for (int i = 0; i < blackStonesLeft; i++) {
            Point p = blackStoneSlots.get(i);
            g2.fillOval(p.x - stoneRadius, p.y - stoneRadius, stoneRadius * 2, stoneRadius * 2);
        }

        if (draggedColor != null && dragPosition != null) {
            g2.setColor(draggedColor);
            g2.fillOval(dragPosition.x - stoneRadius, dragPosition.y - stoneRadius, stoneRadius * 2, stoneRadius * 2);
        }
    }
    // endregion
}
