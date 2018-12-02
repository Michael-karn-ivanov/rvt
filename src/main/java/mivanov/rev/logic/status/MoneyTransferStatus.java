package mivanov.rev.logic.status;

public class MoneyTransferStatus extends OperationStatus {
    public static MoneyTransferStatus SUCCESS = new MoneyTransferStatus(200);
    public static MoneyTransferStatus NOT_ENOUGH_BALANCE = new MoneyTransferStatus(402);
    public static MoneyTransferStatus PLEASE_RETRY = new MoneyTransferStatus(417);
    public static MoneyTransferStatus DEDUCTION_ERROR = new MoneyTransferStatus(500);

    MoneyTransferStatus(int code) { super(code); }
}