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

    protected Node[] findMill(List<Node[]> moves, ImaginativePlayer player) {
        for (Node[] playerMove : moves) {
            if (RuleManager.getInstance().countMillsFormed(playerMove, player) >= 1) {
                return playerMove;
            }
        }
        return null;
    }

    protected Node[] blockMill(List<Node[]> playerMoves, List<Node[]> opponentMoves, ImaginativePlayer opponent) {
        for (Node[] opponentMove : opponentMoves) {
            if (RuleManager.getInstance().countMillsFormed(opponentMove, opponent) >= 1) {
                int targetNodeID = opponentMove[1].getID();
                for (Node[] playerMove : playerMoves) {
                    if (playerMove[1].getID() == targetNodeID) {
                        return playerMove;
                    }
                }
            }
        }
        return null;
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
                        //System.out.println("Real magic does exist");
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

        List<int[]> playerAlmostMills = new ArrayList<>();
        List<Integer> playerStonesID = new ArrayList<>();

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
                playerAlmostMills.add(mill);
            }
        }

        // Finde Züge, bei denen ein Ziel in zwei Almost-Mills gleichzeitig liegt
        for (Node[] move : playerMoves) {
            Node fromNode = move[0];
            Node toNode = move[1];
            int toID = toNode.getID();

            // Prüfe, in wie vielen Almost-Mills das Ziel liegt
            int matchingAlmostMills = 0;
            for (int[] mill : playerAlmostMills) {
                if (isInMill(toID, mill)) {
                    matchingAlmostMills++;
                    if (matchingAlmostMills >= 2) {
                        return new Node[] { fromNode, toNode };
                    }
                }
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

        List<Integer> opponentStones = new ArrayList<>();
        List<int[]> opponentAlmostMills = new ArrayList<>();

        // 1. IDs aller gegnerischen Steine sammeln
        for (Stone stone : opponent.getRemainingStones()) {
            if (stone.getNode() != null) {
                opponentStones.add(stone.getNode().getID());
            }
        }

        // 2. Almost-Mills des Gegners finden (2 Steine + 1 leer)
        for (int[] mill : BoardManager.getInstance().potentialMills) {
            int owned = 0;
            int freeCount = 0;
            int freePos = -1;

            for (int pos : mill) {
                if (opponentStones.contains(pos)) {
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
                opponentAlmostMills.add(mill);
            }
        }

        // 3. Zähle, welche freien Felder mehrfach in diesen Mills vorkommen
        Map<Integer, Integer> dangerZoneCounts = new HashMap<>();

        for (int[] mill : opponentAlmostMills) {
            for (int pos : mill) {
                Node node = BoardManager.getInstance().getNode(pos);
                if (!node.isOccupied() && !opponentStones.contains(pos)) {
                    dangerZoneCounts.put(pos, dangerZoneCounts.getOrDefault(pos, 0) + 1);
                }
            }
        }

        // 4. Wähle das "gefährlichste" freie Feld, das in mehreren gegnerischen
        // Almost-Mills vorkommt
        for (Map.Entry<Integer, Integer> entry : dangerZoneCounts.entrySet()) {
            int pos = entry.getKey();
            int count = entry.getValue();

            if (count >= 2) {
                // 5. Können wir dorthin ziehen? Dann blockieren!
                for (Node[] move : playerMoves) {
                    Node toNode = move[1];
                    if (toNode.getID() == pos) {
                        return move;
                    }
                }
            }
        }

        return null;
    }

    protected Node[] blockBeginnerTrap(List<Node[]> playerMoves, ImaginativePlayer player) {
        if (!(player.getPhase() instanceof SettingPhase && player.getPlacedStones() <= 8)) {
            return null;
        }

        BoardManager boardManager = BoardManager.getInstance();

        // Eckenpaare: {gegnerEcke, gegner gegenüber}
        int[][] cornerPairs = {
                { 0, 23 }, { 2, 21 }, { 6, 17 }, { 8, 15 },
                { 18, 5 }, { 20, 3 }, { 14, 9 }, { 12, 11 }
        };

        for (int[] pair : cornerPairs) {
            int opponentCorner = pair[0];
            int oppositeCorner = pair[1];

            Node opponentNode = boardManager.getNode(opponentCorner);
            Node oppositeNode = boardManager.getNode(oppositeCorner);

            // Wenn Ecke vom Gegner belegt, aber Gegenecke frei
            if (opponentNode.isOccupied() &&
                    !oppositeNode.isOccupied() &&
                    !opponentNode.getStone().getOwner().getName().equals(player.getName())) {

                // Prüfen, ob der Ring ansonsten leer ist
                boolean ringEmpty = true;

                List<Node> ringNodes = boardManager.getRingNodes(opponentCorner);
                for (Node ringNode : ringNodes) {
                    if (ringNode.isOccupied() && ringNode != opponentNode) {
                        ringEmpty = false;
                        break;
                    }
                }

                if (ringEmpty) {
                    // Suche Move, der zu diesem Ziel führt
                    for (Node[] move : playerMoves) {
                        if (move[1].getID() == oppositeCorner) {
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
