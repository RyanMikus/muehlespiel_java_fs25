public class Move {
    private Stone stone;
    private Node fromNode;
    private Node toNode;
    private Stone removedStone;
    private GamePhase currentState;

    /**/
    public Move(Stone stone, Node fromNode, Node toNode, Stone removedStone, GamePhase currentState) {
        this.stone = stone;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.removedStone = removedStone;
        this.currentState = currentState;
    }
}
