import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class GUIBoard extends JPanel {

    private final Map<Integer, Point> nodePositions = new HashMap<>();
    private final Map<Integer, Color> placedStones = new HashMap<>();
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

    public GUIBoard() {
        
        
        setPreferredSize(new Dimension(700, 500));
        setOpaque(false);
        initNodePositions();
        initStoneSlots();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (int i = 0; i < whiteStonesLeft; i++) {
                    if (whiteStoneSlots.get(i).distance(e.getPoint()) < 15) {
                        draggedColor = Color.WHITE;
                        dragPosition = e.getPoint();
                        return;
                    }
                }
                for (int i = 0; i < blackStonesLeft; i++) {
                    if (blackStoneSlots.get(i).distance(e.getPoint()) < 15) {
                        draggedColor = Color.BLACK;
                        dragPosition = e.getPoint();
                        return;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                boolean placed = false;
                if (draggedColor != null) {
                    for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
                        int nodeId = entry.getKey();
                        Point p = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);
                        if (p.distance(e.getPoint()) < 15 && !placedStones.containsKey(nodeId)) {
                            placedStones.put(nodeId, draggedColor);
                            placed = true;
                            break;
                        }
                    }

                    if (placed) {
                        if (draggedColor == Color.WHITE) whiteStonesLeft--;
                        else if (draggedColor == Color.BLACK) blackStonesLeft--;
                    }

                    draggedColor = null;
                    dragPosition = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (draggedColor != null) {
                    dragPosition = e.getPoint();
                    repaint();
                }
            }
        });
    }

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

    private void initStoneSlots() {
        for (int i = 0; i < 9; i++) {
            whiteStoneSlots.add(new Point(25, 80 + i * 42));
            blackStoneSlots.add(new Point(675, 80 + i * 42));
        }
    }

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

        for (Map.Entry<Integer, Color> stoneEntry : placedStones.entrySet()) {
            Point p = nodePositions.get(stoneEntry.getKey());
            int x = offsetX + p.x;
            int y = offsetY + p.y;
            g2.setColor(stoneEntry.getValue());
            g2.fillOval(x - stoneRadius, y - stoneRadius, stoneRadius * 2, stoneRadius * 2);
        }

        if (draggedColor != null && dragPosition != null) {
            g2.setColor(draggedColor);
            g2.fillOval(dragPosition.x - stoneRadius, dragPosition.y - stoneRadius, stoneRadius * 2, stoneRadius * 2);
        }
    }
}
