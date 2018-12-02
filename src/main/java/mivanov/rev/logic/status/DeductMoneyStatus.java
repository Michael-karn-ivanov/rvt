package mivanov.rev.logic.status;

public class DeductMoneyStatus extends OperationStatus {
    public static DeductMoneyStatus SUCCESS = new DeductMoneyStatus(200);
    public static DeductMoneyStatus NOT_ENOUGH_BALANCE = new DeductMoneyStatus(402);
    public static DeductMoneyStatus DEDUCTION_ERROR = new DeductMoneyStatus(500);

    DeductMoneyStatus(int code) { super(code); }
}