import java.util.ArrayList;
import java.util.List;

public class GameTree {
    private RuleManager ruleManager;

    public TreeNode buildGameTree(ImaginativePlayer cpuPlayer, ImaginativePlayer opponentPlayer, byte lookAhead) {
        CPUGameState initialState = CPUGameStateAdapter.fromPlayers(cpuPlayer, opponentPlayer);
        this.ruleManager = cpuPlayer.ruleManager;
        return buildRecursive(initialState, cpuPlayer, opponentPlayer, lookAhead);
    }

    private TreeNode buildRecursive(CPUGameState state, ImaginativePlayer player1, ImaginativePlayer player2,
            byte depth) {
        TreeNode node = new TreeNode(state, null);
        // TODO: undo() has to be implemented and has to be inserted for the tree to
        // form.
        if (depth == 0)
            return node;

        List<Node[]> moves = player1.getAllPossibleMoves();

        for (Node[] move : moves) {
            List<CPUGameState> nextStates = simulateMove(state.copy(), move, player1, player2);
            for (CPUGameState nextState : nextStates) {
                TreeNode child = buildRecursive(nextState, player2, player1, depth--);
                child.setMove(move);
                node.addChild(child);
            }
        }

        return node;
    }

    private List<CPUGameState> simulateMove(CPUGameState state, Node[] move, ImaginativePlayer player1,
            ImaginativePlayer player2) {
        List<CPUGameState> nextStates = new ArrayList<CPUGameState>();
        Node fromNode = move[0];
        Node toNode = move[1];
        // TODO: implement makeMove in ImaginaryPlayer
        // player1.makeMove(fromNode, toNode);
        int formedMills = ruleManager.countMillsFormed(toNode.getStone());
        if (formedMills > 0) {
            while (formedMills > 0) {
                for (Stone stone : player2.getRemainingStones()) {
                    if (ruleManager.isRemovalValid(player1, stone.getNode())) {
                        player2.removeStone(stone);
                        nextStates.add(CPUGameStateAdapter.fromPlayers(player1, player2));
                        player2.addStone(stone);
                    }
                }
                formedMills--;
            }
        } else {
            nextStates.add(CPUGameStateAdapter.fromPlayers(player1, player2));
        }
        return nextStates;
    }
}
