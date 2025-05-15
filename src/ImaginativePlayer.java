import java.util.ArrayList;
import java.util.List;

public class ImaginativePlayer extends PlayerEntity {
    private RuleManager ruleManager;
    private PlayerEntity player;

    public ImaginativePlayer(PlayerEntity playerSelf, RuleManager ruleManager) {
        super(playerSelf.name + " (imaginative)");
        this.player = playerSelf;
        this.ruleManager = ruleManager;
        this.currentPhase = player.currentPhase;
        this.placedStones = player.placedStones;
        for (Stone stone : player.stones) {
            Stone stoneCopy = new Stone(this);
            stoneCopy.setNode(stone.getNode());
            this.addStone(stoneCopy);
        }
    }

    public List<Node[]> getAllPossibleMoves() {
        List<Node[]> possibleMoves = new ArrayList<>();

        if (this.getPhase() instanceof SettingPhase) {
            for (int i = 0; i <= 23; i++) {
                Node node = BoardManager.getInstance().getNode(i);
                if (ruleManager.isMoveValid(this, null, node)) {
                    possibleMoves.add(new Node[]{null, node});
                }
            }
        } else if (this.getPhase() instanceof MovingPhase) {
            for (Stone stone : this.getRemainingStones()) {
                for (Node neighbor : stone.getNode().getNeighbors()) {
                    if (ruleManager.isMoveValid(this, stone.getNode(), neighbor)) {
                        possibleMoves.add(new Node[]{stone.getNode(), neighbor});
                    }
                }
            }
        } else if (this.getPhase() instanceof JumpingPhase) {
            for (Stone stone : this.getRemainingStones()) {
                for (int i = 0; i <= 23; i++) {
                    Node node = BoardManager.getInstance().getNode(i);
                    if (ruleManager.isMoveValid(this, stone.getNode(), node)) {
                        possibleMoves.add(new Node[]{stone.getNode(), node});
                    }
                }
            }
        }

        return possibleMoves;
    }

    public List<Node> getAllPossibleRemovals(PlayerEntity opponent) {
        List<Node> possibleRemovals = new ArrayList<>();
        for (Stone stone : opponent.getRemainingStones()) {
            if (ruleManager.isRemovalValid(this, stone.getNode())) {
                possibleRemovals.add(stone.getNode());
            }
        }
        return possibleRemovals;
    }

    @Override
    public PlayerEntity deepCopy() {
        return null;
    }
}
