import java.util.Map;

public class MoveData {
    public Map<Integer, Integer> boardSnapshot;
    public int noMillTurnCounter;
    public int timeLeft;

    public MoveData(Map<Integer, Integer> boardSnapshot, int noMillTurnCounter, int timeLeft) {
        this.boardSnapshot = boardSnapshot;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }
}
