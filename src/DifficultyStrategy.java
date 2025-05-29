import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public abstract class DifficultyStrategy {

    /**/
    abstract Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent);

    abstract Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent);

    @SafeVarargs
    protected final Node[] tryMoves(Map.Entry<String, Supplier<Node[]>>... strategies) {
        for (Map.Entry<String, Supplier<Node[]>> strategy : strategies) {
            Node[] move = strategy.getValue().get();
            if (move != null) {
                System.out.println("Strategy used: " + strategy.getKey());
                return move;
            }
        }
        return null;
    }

    protected Map.Entry<String, Supplier<Node[]>> entry(String name, Supplier<Node[]> strategy) {
        return new AbstractMap.SimpleEntry<>(name, strategy);
    }

    private boolean isInMill(int id, int[] mill) {
        for (int m : mill) {
            if (m == id)
                return true;
        }
        return false;
    }

    protected Node[] randomMove(List<Node[]> moves) {
        Random random = new Random();
        return moves.get(random.nextInt(moves.size()));
    }

    protected Node[] findMill(List<Node[]> playerMoves, ImaginativePlayer player) {
        for (Node[] playerMove : playerMoves) {
            if (RuleManager.getInstance().countMillsFormed(playerMove, player) >= 1) {
                return playerMove;
            }
        }
        return null;

    }

    protected Node[] blockMill(List<Node[]> playerMoves, List<Node[]> opponentMoves, ImaginativePlayer opponent) {
        List<Integer> almostMills = findAlmostMills(opponent);
        for (Node[] move : playerMoves) {
            if (almostMills.contains(move[1].getID())) {
                for (Node[] opponentMove : opponentMoves) {
                    if (opponentMove[1].getID() == move[1].getID()) {
                        return move;
                    }
                }
            }
        }
        return null;
    }

    private List<Integer> findAlmostMills(ImaginativePlayer player) {
        List<Integer> playerStonesID = new ArrayList<>();
        List<Integer> playerAlmostMills = new ArrayList<>();

        // IDs aller Steine des Spielers sammeln
        for (Stone stone : player.getRemainingStones()) {
            if (stone.getNode() != null) {
                playerStonesID.add(stone.getNode().getID());
            }
        }

        // Finde alle Almost-Mills (2 besetzt, 1 frei)
        for (int[] mill : BoardManager.getInstance().potentialMills) {
            int owned = 0;
            int freeCount = 0;
            int freePos = -1;

            for (int pos : mill) {
                if (playerStonesID.contains(pos)) {
                    owned++;
                } else {
                    Node node = BoardManager.getInstance().getNode(pos);
                    if (!node.isOccupied()) {
                        freeCount++;
                        freePos = pos;
                    }
                }
            }

            if (owned == 2 && freeCount == 1) {
                playerAlmostMills.add(freePos);
            }
        }

        return playerAlmostMills;
    }

    protected Node[] clusterMove(List<Node[]> playerMoves, ImaginativePlayer player) {
        // Find a Node in playerMoves which has a stone of the same player and another
        // unoccupied node as a neighbor.
        for (Node[] playerMove : playerMoves) {
            Node targetNode = playerMove[1];
            for (Node nodeNeighbor : targetNode.getNeighbors()) {
                if (!nodeNeighbor.isOccupied()) {
                    int unoccupiedNeighborCount = 0;
                    int stoneNeighbor = 0;
                    for (Node nodeNeighborNeighbor : nodeNeighbor.getNeighbors()) {
                        if (!nodeNeighborNeighbor.isOccupied()) {
                            unoccupiedNeighborCount++;
                        } else if (nodeNeighborNeighbor.isOccupied()) {
                            for (Stone stone : player.getRemainingStones()) {
                                if (stone.getNode().getID() == nodeNeighborNeighbor.getID())
                                    stoneNeighbor++;
                            }
                        }
                    }
                    if (stoneNeighbor >= 2 && unoccupiedNeighborCount >= 1) {
                        // System.out.println("Real magic does exist");
                        for (Node[] move : playerMoves) {
                            if (move[1].getID() == nodeNeighbor.getID())
                                return move;
                        }
                    }
                }
            }
        }
        return null;
    }

    protected Node[] findZwickMill(List<Node[]> playerMoves, ImaginativePlayer player) {
        if (!(player.getPhase() instanceof SettingPhase && player.getPlacedStones() <= 7
                && player.getPlacedStones() >= 2)) {
            return null;
        }

        List<Integer> placedStonesID = new ArrayList<>();
        for (Stone stone : player.getRemainingStones()) {
            placedStonesID.add(stone.getNode().getID());
        }

        // gather mills that only contains a single stone from the player, none from the
        // opponent
        List<int[]> singleStoneMills = new ArrayList<>();
        for (int[] mill : BoardManager.getInstance().potentialMills) {
            int stoneCounter = 0;
            boolean opponentInMill = false;
            for (int pos : mill) {
                if (placedStonesID.contains(pos)) {
                    stoneCounter++;
                } else { // if it does not contain but is occupied, it belongs to the opponent
                    if (BoardManager.getInstance().getNode(pos).isOccupied()) {
                        opponentInMill = true;
                    }
                }
            }
            if (stoneCounter == 1 && !opponentInMill) {
                singleStoneMills.add(mill);
            }
        }

        // extract positions which are shared in singleStoneMills
        Map<Integer, Integer> posCountMap = new HashMap<>();
        for (int[] mill : singleStoneMills) {
            for (int pos : mill) {
                posCountMap.put(pos, posCountMap.getOrDefault(pos, 0) + 1);
            }
        }
        List<Integer> sharedMillPos = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : posCountMap.entrySet()) {
            if (entry.getValue() >= 2) {
                sharedMillPos.add(entry.getKey());
            }
        }

        // find a player move which is in sharedMillPos
        for (Node[] move : playerMoves) {
            if (sharedMillPos.contains(move[1].getID())) {
                return move;
            }
        }

        return null;
    }

    protected Node[] blockZwickmill(List<Node[]> playerMoves, List<Node[]> opponentMoves, ImaginativePlayer player,
            ImaginativePlayer opponent) {
        if (!(opponent.getPhase() instanceof SettingPhase && opponent.getPlacedStones() <= 7
                && opponent.placedStones >= 2)) {
            return null;
        }

        Node[] zwickMill = findZwickMill(opponentMoves, opponent);
        if (zwickMill == null) {
            return null;
        }

        for (Node[] move : playerMoves) {
            if (move[1].getID() == zwickMill[1].getID()) {
                return move;
            }
        }

        return null;
    }

    protected Node[] blockBeginnerTrap(List<Node[]> playerMoves, ImaginativePlayer player) {
        if (!(player.getPhase() instanceof SettingPhase && player.getPlacedStones() <= 8)) {
            return null;
        }

        BoardManager boardManager = BoardManager.getInstance();

        int[][] cornerPairs = { { 0, 23 }, { 2, 21 }, { 3, 20 }, { 5, 18 }, { 6, 17 }, { 8, 15 } };

        for (int[] pair : cornerPairs) {
            int corner0 = pair[0];
            int corner1 = pair[1];

            Node corner0Node = boardManager.getNode(corner0);
            Node corner1Node = boardManager.getNode(corner1);

            boolean corner0Opponent = false; // is coner0 occupied by opponent
            if (corner0Node.isOccupied() && corner0Node.getStone().getOwner().getName() != player.getName()) {
                corner0Opponent = true;
            }
            boolean corner1Opponent = false; // is coner1 occupied by opponent
            if (corner1Node.isOccupied() && corner1Node.getStone().getOwner().getName() != player.getName()) {
                corner1Opponent = true;
            }

            // get the corner occupied by the opponent, set playerCorner to the opposite
            // corner
            int opponentCorner = -1;
            int playerCorner = -1;
            if (corner0Opponent) {
                opponentCorner = corner0;
                playerCorner = corner1;
            }
            if (corner1Opponent) {
                opponentCorner = corner1;
                playerCorner = corner0;
            }

            if (corner0Opponent || corner1Opponent) {
                boolean ringEmpty = true;
                for (Node ringNode : boardManager.getRingNodes(corner0)) {
                    if (ringNode.getID() != opponentCorner && ringNode.isOccupied()) {
                        ringEmpty = false;
                    }
                }
                if (ringEmpty) {
                    for (Node[] move : playerMoves) {
                        if (move[1].getID() == playerCorner) {
                            return move;
                        }
                    }
                }
            }
        }
        return null;
    }

    protected Node[] useFiggerMill(List<Node[]> playerMoves, ImaginativePlayer player) {
        if (!(player.getPhase() instanceof MovingPhase)) {
            return null;
        }

        List<int[]> playerMills = new ArrayList<>();
        List<int[]> playerAlmostMills = new ArrayList<>();
        List<Integer> playerStonesID = new ArrayList<>();

        // Gather IDs from the nodes player placed stones on.
        for (Stone stone : player.getRemainingStones()) {
            if (stone.getNode() != null) {
                playerStonesID.add(stone.getNode().getID());
            }
        }

        // Suche vollständige und fast vollständige Mühlen
        for (int[] mill : BoardManager.getInstance().potentialMills) {
            int owned = 0;
            int free = -1;

            for (int pos : mill) {
                if (playerStonesID.contains(pos)) {
                    owned++;
                } else {
                    Node node = BoardManager.getInstance().getNode(pos);
                    if (!node.isOccupied()) {
                        free = pos;
                    }
                }
            }

            if (owned == 3) {
                playerMills.add(mill);
            } else if (owned == 2 && free != -1) {
                playerAlmostMills.add(mill);
            }
        }

        // Versuche, eine Almost-Mill durch Ziehen eines Steins aus einer anderen Mill
        // zu vollenden
        for (int[] mill : playerAlmostMills) {
            for (int pos : mill) {
                Node targetNode = BoardManager.getInstance().getNode(pos);
                if (targetNode.isOccupied())
                    continue;

                for (Node[] move : playerMoves) {
                    Node fromNode = move[0];
                    Node toNode = move[1];

                    // Ziel muss in die almost-Mill passen
                    if (toNode.getID() != pos)
                        continue;

                    int fromID = fromNode.getID();

                    // Prüfe, ob fromNode in einer anderen bestehenden Mill liegt
                    boolean fromInAnotherMill = false;
                    for (int[] fullMill : playerMills) {
                        if (isInMill(fromID, fullMill) && !Arrays.equals(fullMill, mill)) {
                            fromInAnotherMill = true;
                            break;
                        }
                    }

                    if (fromInAnotherMill) {
                        return new Node[] { fromNode, toNode };
                    }
                }
            }
        }

        return null;
    }

    protected Node[] openMill(List<Node[]> playerMoves, ImaginativePlayer player, ImaginativePlayer opponent) {
        if (player.getPhase() instanceof SettingPhase) {
            return null;
        }

        // Extract moves from player which move a stone out of a mill
        List<Node[]> fromMillMoves = new ArrayList<>();
        for (Node[] move : playerMoves) {
            for (int[] mill : BoardManager.getInstance().potentialMills) {
                boolean isMill = false;
                for (int pos : mill) {
                    Node node = BoardManager.getInstance().getNode(pos);
                    if (node.isOccupied() && node.getStone().getOwner().getName() == player.getName()) {
                        isMill = true;
                    }
                }
                if (isMill) {
                    fromMillMoves.add(move);
                }
            }
        }

        // Return a move which does not have stone from the opponent as a neighbor to
        // the target node
        for (Node[] move : fromMillMoves) {
            List<Node> neighbors = move[0].getNeighbors();
            boolean opponentNeighbor = false;
            for (Node neighbor : neighbors) {
                if (neighbor.isOccupied() && neighbor.getStone().getOwner().getName() == opponent.getName()) {
                    opponentNeighbor = true;
                }
            }
            if (!opponentNeighbor) {
                return move;
            }
        }

        return null;
    }

    protected Node randomRemoval(List<Node> removals) {
        Random random = new Random();
        return removals.get(random.nextInt(removals.size()));
    }

    protected Node removeMillThreat(List<Node> removableStones, List<Node[]> opponentMoves,
            ImaginativePlayer opponent) {
        Node bestTarget = null;
        int highestThreat = 0;

        for (Node removable : removableStones) {
            int threatScore = 0;

            for (Node[] move : opponentMoves) {
                if (move[0] == null)
                    continue;

                // Ist der entfernbare Stein Teil eines möglichen Zugs?
                if (removable.getID() == move[0].getID()) {
                    int millsFormed = RuleManager.getInstance().countMillsFormed(move, opponent);
                    threatScore += millsFormed;
                }
            }

            // Wähle den Stein, dessen Entfernung den größten Einfluss hätte
            if (threatScore > highestThreat) {
                highestThreat = threatScore;
                bestTarget = removable;
            }
        }

        return bestTarget;
    }
}
