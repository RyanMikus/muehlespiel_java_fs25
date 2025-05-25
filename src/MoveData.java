import java.util.Map;

public class MoveData {
    public int placedStonesWhite;
    public int placedStonesBlack;
    public Map<Integer, Integer> boardSnapshot;
    public int noMillTurnCounter;
    public int timeLeft;

    public MoveData(int placedStonesWhite, int placedStonesBlack, Map<Integer, Integer> boardSnapshot, int noMillTurnCounter, int timeLeft) {
        this.placedStonesWhite = placedStonesWhite;
        this.placedStonesBlack = placedStonesBlack;
        this.boardSnapshot = boardSnapshot;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }
}
