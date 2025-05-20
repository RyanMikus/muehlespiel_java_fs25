import java.util.List;

public class GameStateData {
    public final PlayerData currentPlayer;
    public final PlayerData opponentPlayer;
    public final List<MoveData> undoMoves;
    public final List<MoveData> redoMoves;
    public final int noMillTurnCounter;
    public final int timePerTurn;
    public final int timeLeft;
    public final String gameMode;

    public GameStateData(PlayerData currentPlayer, PlayerData opponentPlayer, List<MoveData> undoMoves, List<MoveData> redoMoves, int noMillTurnCounter, int timePerTurn, int timeLeft, String gameMode) {
        this.currentPlayer = currentPlayer;
        this.opponentPlayer = opponentPlayer;
        this.undoMoves = undoMoves;
        this.redoMoves = redoMoves;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timePerTurn = timePerTurn;
        this.timeLeft = timeLeft;
        this.gameMode = gameMode;
    }
}
