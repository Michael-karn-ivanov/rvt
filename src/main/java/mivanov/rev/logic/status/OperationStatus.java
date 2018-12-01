package mivanov.rev.logic.status;

public abstract class OperationStatus {
    private final int value;

    protected OperationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
