package mivanov.rev.logic.status;

public class AddMoneyStatus extends OperationStatus {
    public static AddMoneyStatus SUCCESS = new AddMoneyStatus(200);

    AddMoneyStatus(int code) { super(code); }
}