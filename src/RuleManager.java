public class RuleManager {
    private static RuleManager instance;

    private RuleManager() {

    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static RuleManager getInstance() {
        if(instance == null) {
            instance = new RuleManager();
        }
        return instance;
    }

    /**/
    public boolean isMoveValid() {
        return false;
    }

    /**/
    public boolean isMillFormed() {
        return false;
    }

    /**/
    public boolean isGameOver() {
        return false;
    }

    /**/
    public boolean isPlayerBlocked() {
        return false;
    }

    /**/
    public boolean isTimeOver() {
        return false;
    }

    /**/
    public boolean isStalemate() {
        return false;
    }
}
