public enum DifficultyLevel {
    NONE,
    EASY,
    MEDIUM,
    HARD;

    public static DifficultyLevel fromStrategyClass(Class<?> strategyClass) {
        return switch (strategyClass.getSimpleName()) {
            case "EasyDifficulty" -> EASY;
            case "MediumDifficulty" -> MEDIUM;
            case "HardDifficulty" -> HARD;
            default -> NONE;
        };
    }
}
