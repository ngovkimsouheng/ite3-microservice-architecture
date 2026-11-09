package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.dto.UserSummary;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UserRepository userRepository;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional(readOnly = true)
    public PageResult<UserSummary> execute(int page, int size, String sort) {
        PageResult<User> userPage = userRepository.findAll(page, size, sort);

        List<UserSummary> content = userPage.content().stream()
                .map(domainDtoMapper::toUserSummary)
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
