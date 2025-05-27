import java.util.List;

public class PlayerData {
    public String name;
    public String phase;
    public int placedStones;
    public List<Integer> stonePositions;
    public String type;
    public DifficultyLevel difficulty;

    public PlayerData(String name, String phase, int placedStones, List<Integer> stonePositions, String type, DifficultyLevel difficulty) {
        this.name = name;
        this.phase = phase;
        this.placedStones = placedStones;
        this.stonePositions = stonePositions;
        this.type = type;
        this.difficulty = difficulty;
    }
}
