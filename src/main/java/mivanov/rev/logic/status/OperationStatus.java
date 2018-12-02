package mivanov.rev.logic.status;

public abstract class OperationStatus {
    private final int value;

    protected OperationStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof OperationStatus)) return false;
        OperationStatus status = (OperationStatus)obj;
        return value == status.value;
    }
}
