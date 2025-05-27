public class Player extends PlayerEntity {

    public Player(String name) {
        super(name);
    }

    /**
     * Creates a deep Copy of this Player.
     *
     * @return A new Object of this Player with all his components.
     */
    @Override
    public PlayerEntity deepCopy() {
        // 1. Kopie Objekt erzeugen und Namen des Original übergeben
        Player copy = new Player(this.name);

        // 2. Steine tief kopieren (falls Liste nicht null)
        if (this.stones != null) {
            for (Stone originalStone : this.stones) {
                if (originalStone != null) {
                    Stone copiedStone = originalStone.deepCopy(copy);
                    copy.addStone(copiedStone);
                }
            }
        }

        // 3. Phase tief kopieren, falls vorhanden
        if (this.currentPhase != null) {
            GamePhase copiedPhase = this.currentPhase.deepCopy(copy);
            copy.setPhase(copiedPhase);
        }

        // 4. Platzierte Steine übernehmen
        copy.placedStones = this.placedStones;
        return copy;
    }

    @Override
    public void setPlayer(PlayerEntity player) {
        this.name = player.getName();
        this.stones.clear();
        for (Stone stone : player.getRemainingStones()){
            this.stones.add(stone.deepCopy(this));
        }
        this.currentPhase = player.getPhase().deepCopy(this);
        this.placedStones = player.placedStones;
    }
}
