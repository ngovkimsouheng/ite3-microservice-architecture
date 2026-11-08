package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UserSummary;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserSummary> execute(int page, int size, String sort) {
        PageResult<User> userPage = userRepository.findAll(page, size, sort);

        List<UserSummary> content = userPage.content().stream()
                .map(u -> new UserSummary(
                        u.getUuid(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getGivenName(),
                        u.getFamilyName(),
                        u.isEnabled()
                ))
                .toList();

        return new PageResult<>(
                content,
                userPage.pageNumber(),
                userPage.pageSize(),
                userPage.totalElements(),
                userPage.totalPages()
        );
    }
}