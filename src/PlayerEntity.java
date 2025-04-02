import java.util.List;

public abstract class PlayerEntity {
    private String name;
    private List<Stone> stones;
    private GameState currentState;

    /**/
    public abstract void makeMove();

    /**/
    public void handleMove() {

    }

    /**/
    public void removeStone() {

    }

    /**/
    public void setState(GameState state) {

    }

    /**/
    public boolean allStonesPlaced() {
        return false;
    }

    /**/
    public List<Stone> getRemainingStones() {
        return null;
    }

    /**/
    public String getName() {
        return name;
    }
}
