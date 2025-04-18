import java.util.List;

public class CPUGameStateAdapter {

    public static CPUGameState fromPlayers(PlayerEntity cpu, PlayerEntity opponent) {
        byte[] cpuStones = getStonePositions(cpu);
        byte[] opponentStones = getStonePositions(opponent);

        char cpuState = getPhaseChar(cpu.getPhase());
        char opponentState = getPhaseChar(opponent.getPhase());

        return new CPUGameState(cpuStones, opponentStones, cpuState, opponentState);
    }

    private static byte[] getStonePositions(PlayerEntity player) {
        List<Stone> stones = player.getRemainingStones();
        byte[] positions = new byte[stones.size()];
        for (int i = 0; i < stones.size(); i++) {
            positions[i] = (byte) stones.get(i).getNode().getID();
        }
        return positions;
    }

    private static char getPhaseChar(GamePhase phase) {
        if (phase instanceof SettingPhase)
            return 's';
        if (phase instanceof MovingPhase)
            return 'm';
        if (phase instanceof JumpingPhase)
            return 'j';
        return 'x'; // Unknown or game over
    }
}
