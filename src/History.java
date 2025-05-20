import java.util.Map;
import java.util.Stack;

public class History {
    private Stack<Move> undoMoves = new Stack<>();
    private Stack<Move> redoMoves = new Stack<>();

    /**
     * Stores a performed move containing the state of the game to a Stack.
     * <p>
     * Clears the redo stack, as this method gets only called when performing a new move,
     * invalidating the other "reality".
     *
     * @param move The move that should be stored.
     */
    public void addMove(Move move) {
        undoMoves.push(move);
        clearRedo();


//        System.out.println("[HISTORY] Neuer Move gespeichert → UndoStack Größe: " + undoMoves.size());
//        System.out.println("[HISTORY] Gespeichertes Board mit Knotenreferenzen: ");
//        Map<Integer, Node> snapshot = move.getBoardSnapshot();
//        for (Node node : snapshot.values()) {
//            if (node.getStone() != null) {
//                String owner = (node.getStone() == null) ? "-" : node.getStone().getOwner().getName();
//                String line = String.format("ID: %2d, Owner: %s", node.getID(), owner);
//                System.out.println(line);
//            }
//        }

    }

    /**
     * Undoes the last move by popping it from the undo stack and pushing it onto the redo stack.
     * <p>
     * Returns the new top of the undo stack, representing the previous game state.
     * If there are not enough moves to undo, returns null.
     *
     * @return The previous game state (second last move), or null if undo is not possible.
     */
    public Move undoMove() {
        if(undoMoves.size() > 1) {
            Move undoMove = undoMoves.pop();
            redoMoves.push(undoMove);
            //System.out.println("[HISTORY] - Undo aufgerufen");
            return undoMoves.peek();
        }
        return null;
    }

    /**
     * Redoes the last undone move by popping it from the redo stack and pushing it back onto the undo stack.
     * <p>
     * Returns the move that was redone to restore the next game state.
     * If there is no move to redo, returns null.
     *
     * @return The move representing the next game state, or null if redo is not possible.
     */
    public Move redoMove() {
        if (!redoMoves.empty()) {
            Move redoMove = redoMoves.pop();
            undoMoves.push(redoMove);
            //System.out.println("[HISTORY] RedoMove aufgerufen → RedoStack Größe danach: " + redoMoves.size());
            return redoMove;
        }
    
        System.out.println("[HISTORY] RedoMove → Stack war leer");
        return null;
    }

    /**
     * Clears the redo stack by removing every element.
     */
    private void clearRedo() {
        //System.out.println("[HISTORY] clearRedo() aufgerufen – RedoStack Größe: " + redoMoves.size());
        if (!redoMoves.isEmpty()) {
            System.out.println("[HISTORY] RedoStack wird geleert (" + redoMoves.size() + " Einträge)");
            redoMoves.clear();
        }
    }


    public Move peekUndo() {
        if (!undoMoves.isEmpty()) {
            return undoMoves.peek();
        }
        return null;
    }
}
