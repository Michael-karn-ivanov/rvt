package mivanov.rev.logic.status;

public class DeductMoneyStatus extends OperationStatus {
    public static DeductMoneyStatus SUCCESS = new DeductMoneyStatus(200);
    public static DeductMoneyStatus NOT_ENOUGH_BALANCE = new DeductMoneyStatus(404);

    DeductMoneyStatus(int code) { super(code); }
}