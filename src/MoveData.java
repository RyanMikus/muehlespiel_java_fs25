import java.util.Map;

public class MoveData {
    public int placedStonesWhite;
    public int placedStonesBlack;
    public Map<Integer, Integer> boardSnapshot;
    public boolean currentIsWhite;
    public String phaseWhite;
    public String phaseBlack;
    public int noMillTurnCounter;
    public int timeLeft;

    public MoveData(int placedStonesWhite, int placedStonesBlack, Map<Integer, Integer> boardSnapshot, boolean currentIsWhite, String phaseWhite, String phaseBlack, int noMillTurnCounter, int timeLeft) {
        this.placedStonesWhite = placedStonesWhite;
        this.placedStonesBlack = placedStonesBlack;
        this.boardSnapshot = boardSnapshot;
        this.currentIsWhite = currentIsWhite;
        this.phaseWhite = phaseWhite;
        this.phaseBlack = phaseBlack;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }
}
