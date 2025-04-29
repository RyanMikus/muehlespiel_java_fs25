import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private CPUGameState state;
    private Node[] move;
    private final List<TreeNode> children = new ArrayList<>();

    public TreeNode(CPUGameState state, Node[] move) {
        this.state = state;
        this.move = move;
    }

    public CPUGameState getState() {
        return state;
    }

    public Node[] getMove() {
        return move;
    }

    public void setMove(Node[] move) {
        this.move = move;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }
}
