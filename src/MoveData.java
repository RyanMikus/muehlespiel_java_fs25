import java.util.Map;

public class MoveData {
    public PlayerData currentPlayer;
    public PlayerData opponentPlayer;
    public Map<Integer, Integer> boardSnapshot;
    public int noMillTurnCounter;
    public int timeLeft;

    public MoveData(PlayerData currentPlayer, PlayerData opponentPlayer,
                    Map<Integer, Integer> boardSnapshot,
                    int noMillTurnCounter, int timeLeft) {
        this.currentPlayer = currentPlayer;
        this.opponentPlayer = opponentPlayer;
        this.boardSnapshot = boardSnapshot;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }
}
