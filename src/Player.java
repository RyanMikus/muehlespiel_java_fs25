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
        //System.out.println("[DEBUG] Starte Kopiervorgang für Spieler: " + this.name);

        // 1. Neuen Spieler mit gleichem Namen erzeugen
        Player copy = new Player(this.name);
        //System.out.println("[DEBUG] Neuer Spieler erstellt mit Name: " + copy.name);

        // 2. Steine tief kopieren (falls Liste nicht null)
        if (this.stones != null) {
            //System.out.println("[DEBUG] Kopiere " + this.stones.size() + " Steine für Spieler: " + this.name);
            for (int i = 0; i < this.stones.size(); i++) {
                Stone originalStone = this.stones.get(i);
                if (originalStone != null) {
                    Stone copiedStone = originalStone.deepCopy(copy); // ← Kopie erzeugen
                    copy.addStone(copiedStone);                        // ← Kopie dem neuen Spieler hinzufügen
                    //System.out.println("[DEBUG] -> Stein " + i + " kopiert: " + originalStone + " → " + copiedStone);
                } else {
                    //System.out.println("[WARN] -> Stein " + i + " war null und wurde übersprungen.");
                }
            }
        } else {
            //System.out.println("[WARN] Steine-Liste war null bei Spieler " + this.name);
        }

        // 3. Phase tief kopieren, falls vorhanden
        if (this.currentPhase != null) {
            //System.out.println("[DEBUG] Kopiere Phase: " + this.currentPhase.getClass().getSimpleName());
            GamePhase copiedPhase = this.currentPhase.deepCopy(copy);
            copy.setPhase(copiedPhase);
        } else {
            //System.out.println("[WARN] Phase war null bei Spieler " + this.name);
            copy.setPhase(null);
        }

        // 4. Platzierte Steine übernehmen
        copy.placedStones = this.placedStones;
        //System.out.println("[DEBUG] Platzierte Steine übernommen: " + this.placedStones);

        // 5. Abschluss
        //System.out.println("[DEBUG] Kopiervorgang für Spieler '" + this.name + "' abgeschlossen.\n");

        return copy;
    }
}
