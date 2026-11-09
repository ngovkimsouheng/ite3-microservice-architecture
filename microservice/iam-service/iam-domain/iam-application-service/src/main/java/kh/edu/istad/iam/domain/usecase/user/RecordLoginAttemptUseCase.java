package kh.edu.istad.iam.domain.usecase.user;

public interface RecordLoginAttemptUseCase {
    void execute(String usernameOrEmail, boolean success);
}
