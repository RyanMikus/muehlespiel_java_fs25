import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class PlayerEntity {
    protected String name;
    protected List<Stone> stones;
    protected GamePhase currentPhase;
    protected int placedStones = 0;

    /**
     * Constructs a player with the given name.
     * Initializes an empty list for its stones.
     * Sets the phase, always starting with the setting phase.
     *
     * @param name The name of the player.
     */
    protected PlayerEntity(String name) {
        this.name = name;
        this.stones = new ArrayList<>();
        this.currentPhase = new SettingPhase(this);
    }

    /**
     * Adds a stone to the players stone list.
     *
     * @param stone The stone that gets added to the list.
     */
    public void addStone(Stone stone) {
        stones.add(stone);
    }

    /**
     * Removes a stone from the player.
     *
     * @param stone The stone that should get removed.
     */
    public void removeStone(Stone stone) {
        stones.remove(stone);
    }

    /**
     * Counts how many stones of the player have already been placed.
     */
    public void stonePlaced() {
        placedStones++;
    }

    /**
     * Checks if all the stones of the player have been placed already.
     *
     * @return true if all stones have been placed, false otherwise.
     */
    public boolean allStonesPlaced() {
        return placedStones == 9;
    }

    /**
     * Getter for the list containing all the players remaining stones.
     *
     * @return List containing all remaining stones.
     */
    public List<Stone> getRemainingStones() {
        return stones;
    }

    /**
     * Getter for the players name.
     *
     * @return name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the current phase the player is in.
     *
     * @return Current phase the player is in.
     */
    public GamePhase getPhase() {
        return currentPhase;
    }

    public int getPlacedStones() {
        return placedStones;
    }

    /**
     * Changes the current phase to a new one.
     *
     * @param phase The new phase that should be changed to.
     */
    public void setPhase(GamePhase phase) {
        currentPhase = phase;
    }


    /**
     * Creates a deep Copy of a Player inclusive Stones and Phase.
     * 
     * @return Deep Copy of a new Player
     */
    public abstract PlayerEntity deepCopy();

    public abstract void setPlayer(PlayerEntity player);

    public void printCopy(){
        System.out.printf(" Player %s | %s | %d\n", this.name, this.currentPhase.getClass().getSimpleName(), this.placedStones);
        for(Stone stone : this.stones){
            System.out.printf("Stone ID : %d | Node : %d |Owner : %s\n", stone.getNode().getID(),stone.getNode().getID(), stone.getOwner().getName());
        }
    }


}
