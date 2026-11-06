package kh.edu.istad.iam.application.usecase.user;

public interface RecordLoginAttemptUseCase {
    void execute(String usernameOrEmail, boolean success);
}
