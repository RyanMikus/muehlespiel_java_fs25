import java.util.List;

public class GameStateData {
    public final PlayerData whiteData;
    public final PlayerData blackData;
    public boolean currentIsWhite;
    public final List<MoveData> undoMoves;
    public final List<MoveData> redoMoves;
    public final int noMillTurnCounter;
    public final int timePerTurn;
    public final int timeLeft;
    public final String gameMode;

    public GameStateData(PlayerData whiteData, PlayerData blackData, boolean currentIsWhite, List<MoveData> undoMoves, List<MoveData> redoMoves, int noMillTurnCounter, int timePerTurn, int timeLeft, String gameMode) {
        this.whiteData = whiteData;
        this.blackData = blackData;
        this.currentIsWhite = currentIsWhite;
        this.undoMoves = undoMoves;
        this.redoMoves = redoMoves;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timePerTurn = timePerTurn;
        this.timeLeft = timeLeft;
        this.gameMode = gameMode;
    }
}
