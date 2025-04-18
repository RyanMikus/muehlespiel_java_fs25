import java.util.Arrays;
import java.util.Objects;

public class CPUGameState {
    // Optimized for low memory: byte[] instead of int[]
    private byte[] cpuStones, opponentStones;
    private char cpuState, opponentState;

    public CPUGameState(byte[] cpuStones, byte[] opponentStones, char cpuState, char opponentState) {
        this.cpuStones = Arrays.copyOf(cpuStones, cpuStones.length);
        this.opponentStones = Arrays.copyOf(opponentStones, opponentStones.length);
        this.cpuState = cpuState;
        this.opponentState = opponentState;
    }

    // Getter
    public byte[] getCpuStones() {
        return Arrays.copyOf(cpuStones, cpuStones.length);
    }

    public byte[] getOpponentStones() {
        return Arrays.copyOf(opponentStones, opponentStones.length);
    }

    public char getCpuState() {
        return cpuState;
    }

    public char getOpponentState() {
        return opponentState;
    }

    // Setter
    public void setCpuStones(byte[] cpuStones) {
        this.cpuStones = Arrays.copyOf(cpuStones, cpuStones.length);
    }

    public void setOpponentStones(byte[] opponentStones) {
        this.opponentStones = Arrays.copyOf(opponentStones, opponentStones.length);
    }

    public void setCpuState(char cpuState) {
        this.cpuState = cpuState;
    }

    public void setOpponentState(char opponentState) {
        this.opponentState = opponentState;
    }

    // Copy method
    public CPUGameState copy() {
        return new CPUGameState(cpuStones, opponentStones, cpuState, opponentState);
    }

    // equals and hashCode for graph use
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CPUGameState))
            return false;
        CPUGameState other = (CPUGameState) obj;
        return Arrays.equals(cpuStones, other.cpuStones)
                && Arrays.equals(opponentStones, other.opponentStones)
                && cpuState == other.cpuState
                && opponentState == other.opponentState;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(cpuState, opponentState);
        result = 31 * result + Arrays.hashCode(cpuStones);
        result = 31 * result + Arrays.hashCode(opponentStones);
        return result;
    }

    // For debugging
    @Override
    public String toString() {
        return "CPUGameState{" +
                "cpuStones=" + Arrays.toString(cpuStones) +
                ", opponentStones=" + Arrays.toString(opponentStones) +
                ", cpuState=" + cpuState +
                ", opponentState=" + opponentState +
                '}';
    }
}
