package mivanov.rev.logic.status;

public class CreateUserStatus extends OperationStatus {
    public static CreateUserStatus CREATED = new CreateUserStatus(201);
    public static CreateUserStatus ALREADY_EXISTS = new CreateUserStatus(409);

    CreateUserStatus(int code) { super(code); }
}