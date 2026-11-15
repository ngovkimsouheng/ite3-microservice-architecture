package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.dto.UserSummary;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public PageResult<UserSummary> execute(int page, int size, String sort) {
        PageResult<User> userPage = userRepository.findAll(page, size, sort);

        List<UserSummary> content = userPage.content().stream()
                .map(user -> {
                    List<String> roleNames = new ArrayList<>();
                    if (user.getId() != null) {
                        List<UserRoleAssignment> assignments = userRepository.findRoleAssignmentsByUserId(user.getId());
                        for (UserRoleAssignment assignment : assignments) {
                            if (assignment.isActive()) {
                                roleRepository.findById(assignment.getRoleId())
                                        .filter(r -> r.isActive() && !r.isDeleted())
                                        .ifPresent(r -> roleNames.add(r.getName()));
                            }
                        }
                    }
                    return new UserSummary(
                            user.getUuid(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getGivenName(),
                            user.getFamilyName(),
                            user.getPhoneNumber(),
                            user.getLocale(),
                            user.isEnabled(),
                            user.isAccountNonLocked(),
                            roleNames,
                            user.getCreatedAt()
                    );
                })
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
