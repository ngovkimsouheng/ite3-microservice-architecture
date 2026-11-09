package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.domain.dto.*;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserPermissionAssignment;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import kh.edu.istad.iam.persistence.entity.UserJpaEntity;
import kh.edu.istad.iam.persistence.mapper.UserPersistenceMapper;
import kh.edu.istad.iam.persistence.repository.UserJpaRepository;
import kh.edu.istad.iam.persistence.repository.UserPermissionAssignmentJpaRepository;
import kh.edu.istad.iam.persistence.repository.UserRoleAssignmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserRoleAssignmentJpaRepository userRoleRepo;
    private final UserPermissionAssignmentJpaRepository userPermRepo;
    private final UserPersistenceMapper userMapper;

    @Override
    public PageResult<User> findAll(int page, int size, String sort) {
        Sort sortObj = (sort != null && !sort.isBlank()) ? Sort.by(sort) : Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<UserJpaEntity> entityPage = userJpaRepository.findAllByIsDeletedFalse(pageable);
        List<User> content = entityPage.getContent().stream().map(userMapper::toDomain).toList();

        return new PageResult<>(
                content,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public User save(User user) {
        return userMapper.toDomain(userJpaRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userJpaRepository.findByUuid(uuid).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userJpaRepository.existsByPhoneNumberAndIsDeletedFalse(phoneNumber);
    }

    @Override
    public void saveRoleAssignment(UserRoleAssignment a) {
        userRoleRepo.save(userMapper.toEntity(a));
    }

    @Override
    public List<UserRoleAssignment> findRoleAssignmentsByUserId(Long userId) {
        return userRoleRepo.findByUserId(userId).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UserRoleAssignment> findRoleAssignment(Long userId, Long roleId) {
        return userRoleRepo.findByUserIdAndRoleId(userId, roleId)
                .map(userMapper::toDomain);
    }

    @Override
    public void savePermissionAssignment(UserPermissionAssignment a) {
        userPermRepo.save(userMapper.toEntity(a));
    }

    @Override
    public List<UserPermissionAssignment> findPermissionAssignmentsByUserId(Long userId) {
        return userPermRepo.findByUserId(userId).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UserPermissionAssignment> findPermissionAssignment(Long userId, Long permissionId) {
        return userPermRepo.findByUserIdAndPermissionId(userId, permissionId)
                .map(userMapper::toDomain);
    }
}
