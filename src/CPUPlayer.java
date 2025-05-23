public class CPUPlayer extends PlayerEntity {
    private DifficultyStrategy strategy;
    private PlayerEntity opponent;

    public CPUPlayer(String name, PlayerEntity opponent) {
        super(name);
        this.opponent = opponent;
        this.strategy = null;
    }

    public Node[] makeMove() {
        RuleManager ruleManager = RuleManager.getInstance();
        ImaginativePlayer imaginativeSelf = new ImaginativePlayer(this);
        ImaginativePlayer imaginativeOpponent = new ImaginativePlayer(opponent);
        // strategy.buildGameTree(imaginativeSelf, imaginativeOpponent);
        Node[] calculatedMove = strategy.calculateMove(imaginativeSelf, imaginativeOpponent);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            System.out.println("[INFO] - Sleep interrupted, exiting early.");
            return null;
        }
        return calculatedMove;
    }

    public Node makeRemove() {
        RuleManager ruleManager = RuleManager.getInstance();
        ImaginativePlayer imaginativeSelf = new ImaginativePlayer(this);
        ImaginativePlayer imaginativeOpponent = new ImaginativePlayer(opponent);
        Node calculatedRemoval = strategy.calculateRemoval(imaginativeSelf, imaginativeOpponent);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            System.out.println("[INFO] - Sleep interrupted, exiting early.");
            return null;
        }
        return calculatedRemoval;
    }

    public void setDifficulty(DifficultyStrategy strategy) {
        this.strategy = strategy;
        System.out.println("set Strategy : " + strategy.getClass().getSimpleName());
    }

    @Override
    public PlayerEntity deepCopy() {
        // System.out.println("[DEBUG] Starte Kopiervorgang für Spieler: " + this.name);
        PlayerEntity opponentCopy = opponent.deepCopy();
        // 1. Neuen Spieler mit gleichem Namen erzeugen
        CPUPlayer copy = new CPUPlayer(this.name, opponentCopy);
        // System.out.println("[DEBUG] Neuer Spieler erstellt mit Name: " + copy.name);

        // 2. Steine tief kopieren (falls Liste nicht null)
        if (this.stones != null) {
            // System.out.println("[DEBUG] Kopiere " + this.stones.size() + " Steine für
            // Spieler: " + this.name);
            for (int i = 0; i < this.stones.size(); i++) {
                Stone originalStone = this.stones.get(i);
                if (originalStone != null) {
                    Stone copiedStone = originalStone.deepCopy(copy); // ← Kopie erzeugen
                    copy.addStone(copiedStone); // ← Kopie dem neuen Spieler hinzufügen
                    // System.out.println("[DEBUG] -> Stein " + i + " kopiert: " + originalStone + "
                    // → " + copiedStone);
                } else {
                    // System.out.println("[WARN] -> Stein " + i + " war null und wurde
                    // übersprungen.");
                }
            }
        } else {
            // System.out.println("[WARN] Steine-Liste war null bei Spieler " + this.name);
        }

        // 3. Phase tief kopieren, falls vorhanden
        if (this.currentPhase != null) {
            // System.out.println("[DEBUG] Kopiere Phase: " +
            // this.currentPhase.getClass().getSimpleName());
            GamePhase copiedPhase = this.currentPhase.deepCopy(copy);
            copy.setPhase(copiedPhase);
        } else {
            // System.out.println("[WARN] Phase war null bei Spieler " + this.name);
            copy.setPhase(null);
        }

        // 4. Platzierte Steine übernehmen
        copy.placedStones = this.placedStones;
        // System.out.println("[DEBUG] Platzierte Steine übernommen: " +
        // this.placedStones);

        // 5. Abschluss
        // System.out.println("[DEBUG] Kopiervorgang für Spieler '" + this.name + "'
        // abgeschlossen.\n");

        return copy;
    }

    @Override
    public void setPlayer(PlayerEntity player) {
        this.name = player.getName();
        this.stones.clear();
        for (Stone stone : player.getRemainingStones()) {
            this.stones.add(stone.deepCopy(this));
        }
        this.currentPhase = player.getPhase().deepCopy(this);
        this.placedStones = player.placedStones;
    }
}