package mivanov.rev.logic.status;

public class GetUserStatus extends OperationStatus {
    public static GetUserStatus USER_FOUND = new GetUserStatus(200);
    public static GetUserStatus USER_DOESNT_EXIST = new GetUserStatus(404);

    GetUserStatus(int code) { super(code); }
}
