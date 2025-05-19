import java.util.List;

public class PlayerData {
    public String name;
    public String phase;
    public int placedStones;
    public List<Integer> stonePositions;
    public String type;

    public PlayerData(String name, String phase, int placedStones, List<Integer> stonePositions, String type) {
        this.name = name;
        this.phase = phase;
        this.placedStones = placedStones;
        this.stonePositions = stonePositions;
        this.type = type;
    }
}
