import java.util.List;

public class GameStateData {
    public final PlayerData currentPlayer;
    public final PlayerData opponentPlayer;
    public final List<MoveData> undoMoves;
    public final List<MoveData> redoMoves;
    public final int noMillTurnCounter;
    public final int timeLeft;
    public final String gameMode;

    public GameStateData(PlayerData currentPlayer, PlayerData opponentPlayer, List<MoveData> undoMoves, List<MoveData> redoMoves, int noMillTurnCounter, int timeLeft, String gameMode) {
        this.currentPlayer = currentPlayer;
        this.opponentPlayer = opponentPlayer;
        this.undoMoves = undoMoves;
        this.redoMoves = redoMoves;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
        this.gameMode = gameMode;
    }
}
