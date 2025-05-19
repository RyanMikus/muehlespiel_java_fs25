public class UndoRedoTest {

    public static void main(String[] args) {
        // === Setup ===
        GameManager gameManager = GameManager.getInstance();
        BoardManager boardManager = BoardManager.getInstance();

        TurnTimer timer = new TurnTimer(30, () -> {
        }, () -> {
        });
        gameManager.setTurnTimer(timer);

        History history = new History();

        boardManager.initializeBoard(); // frisches Board

        Player playerWhite = new Player("white");
        Player playerBlack = new Player("black");

        // ✅ Initialzustand speichern
        Move initialMove = new Move(
                playerWhite.deepCopy(),
                playerBlack.deepCopy(),
                boardManager.copyBoard(playerWhite.deepCopy(), playerBlack.deepCopy()),
                gameManager.getNoMillTurnCounter(),
                timer.getTimeLeft());
        history.addMove(initialMove);

        gameManager.setCurrentPlayer(playerWhite);
        gameManager.setOpponent(playerBlack);

        // === Zug 1: Weiß setzt einen Stein ===
        Stone stone1 = new Stone(playerWhite);
        Node node0 = boardManager.getNode(0);
        node0.placeStone(stone1);
        playerWhite.addStone(stone1);
        playerWhite.stonePlaced();

        Move move1 = new Move(
                playerWhite.deepCopy(),
                playerBlack.deepCopy(),
                boardManager.copyBoard(playerWhite.deepCopy(), playerBlack.deepCopy()),
                gameManager.getNoMillTurnCounter(),
                timer.getTimeLeft());
        history.addMove(move1);

        System.out.println("\n--- Aktuelles Spielfeld nach zug 1 ---");
        //boardManager.printBoard();

        // === Zug 2: Schwarz setzt einen Stein ===
        gameManager.setCurrentPlayer(playerBlack);
        gameManager.setOpponent(playerWhite);

        Stone stone2 = new Stone(playerBlack);
        Node node1 = boardManager.getNode(1);
        node1.placeStone(stone2);
        playerBlack.addStone(stone2);
        playerBlack.stonePlaced();

        Move move2 = new Move(
                playerBlack.deepCopy(),
                playerWhite.deepCopy(),
                boardManager.copyBoard(playerBlack.deepCopy(), playerWhite.deepCopy()),
                gameManager.getNoMillTurnCounter(),
                timer.getTimeLeft());
        history.addMove(move2);

        System.out.println("\n--- Aktuelles Spielfeld nach Spielzug 2 ---");
        //boardManager.printBoard();

        // === UNDO ===
        System.out.println("\n--- UNDO 1---");
        Move previousMove = history.undoMove();
        if (previousMove != null) {
            gameManager.loadMove(previousMove);
            System.out.println("-> Spiel zurückgesetzt (UNDO).");
        } else {
            System.out.println("-> UNDO nicht möglich.");
        }

        System.out.println("\n--- Aktuelles Spielfeld  nach Undo 1---");
        //boardManager.printBoard();

        // === UNDO ===
        System.out.println("\n--- UNDO 2---");
        previousMove = history.undoMove();
        if (previousMove != null) {
            gameManager.loadMove(previousMove);
            System.out.println("-> Spiel zurückgesetzt (UNDO).");
        } else {
            System.out.println("-> UNDO nicht möglich.");
        }

        System.out.println("\n--- Aktuelles Spielfeld  nach Undo 2---");
        //boardManager.printBoard();

        // === REDO ===
        System.out.println("\n--- REDO 1---");
        Move redoMove = history.redoMove();
        if (redoMove != null) {
            gameManager.loadMove(redoMove);
            System.out.println("-> Spiel wiederhergestellt (REDO).");
        } else {
            System.out.println("-> REDO nicht möglich.");
        }

        // === Aktuellen Zustand anzeigen ===
        System.out.println("\n--- Aktuelles Spielfeld nach Redo 1---");
        //boardManager.printBoard();

        // === REDO ===
        System.out.println("\n--- REDO 2---");
        redoMove = history.redoMove();
        if (redoMove != null) {
            gameManager.loadMove(redoMove);
            System.out.println("-> Spiel wiederhergestellt (REDO).");
        } else {
            System.out.println("-> REDO nicht möglich.");
        }

        // === Aktuellen Zustand anzeigen ===
        System.out.println("\n--- Aktuelles Spielfeld nach Redo 2---");
        //boardManager.printBoard();
    }
}
