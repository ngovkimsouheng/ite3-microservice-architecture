package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.application.dto.CommonDtos;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserPermissionAssignment;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import kh.edu.istad.iam.persistence.entity.UserJpaEntity;
import kh.edu.istad.iam.persistence.entity.UserPermissionAssignmentJpaEntity;
import kh.edu.istad.iam.persistence.entity.UserRoleAssignmentJpaEntity;
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

    @Override
    public CommonDtos.PageResult<User> findAll(int page, int size, String sort) {
        Sort sortObj = (sort != null && !sort.isBlank()) ? Sort.by(sort) : Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<UserJpaEntity> entityPage = userJpaRepository.findAllByIsDeletedFalse(pageable);

        List<User> content = entityPage.getContent().stream()
                .map(this::mapToDomain)
                .toList();

        return new CommonDtos.PageResult<>(
                content,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapToJpa(user);
        return mapToDomain(userJpaRepository.save(entity));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userJpaRepository.findByUuid(uuid).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::mapToDomain);
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
        userRoleRepo.save(UserRoleAssignmentJpaEntity.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .roleId(a.getRoleId())
                .assignedAt(a.getAssignedAt())
                .assignedBy(a.getAssignedBy())
                .expiresAt(a.getExpiresAt())
                .revokedAt(a.getRevokedAt())
                .revokedBy(a.getRevokedBy())
                .reason(a.getReason())
                .build());
    }

    @Override
    public List<UserRoleAssignment> findRoleAssignmentsByUserId(Long userId) {
        return userRoleRepo.findByUserId(userId).stream()
                .map(e -> UserRoleAssignment.builder()
                        .id(e.getId())
                        .userId(e.getUserId())
                        .roleId(e.getRoleId())
                        .assignedAt(e.getAssignedAt())
                        .assignedBy(e.getAssignedBy())
                        .expiresAt(e.getExpiresAt())
                        .revokedAt(e.getRevokedAt())
                        .revokedBy(e.getRevokedBy())
                        .reason(e.getReason())
                        .build())
                .toList();
    }

    @Override
    public Optional<UserRoleAssignment> findRoleAssignment(Long userId, Long roleId) {
        return userRoleRepo.findByUserIdAndRoleId(userId, roleId)
                .map(e -> UserRoleAssignment.builder()
                        .id(e.getId())
                        .userId(e.getUserId())
                        .roleId(e.getRoleId())
                        .assignedAt(e.getAssignedAt())
                        .assignedBy(e.getAssignedBy())
                        .expiresAt(e.getExpiresAt())
                        .revokedAt(e.getRevokedAt())
                        .revokedBy(e.getRevokedBy())
                        .reason(e.getReason())
                        .build());
    }

    @Override
    public void savePermissionAssignment(UserPermissionAssignment a) {
        userPermRepo.save(UserPermissionAssignmentJpaEntity.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .permissionId(a.getPermissionId())
                .effect(a.getEffect())
                .assignedAt(a.getAssignedAt())
                .assignedBy(a.getAssignedBy())
                .expiresAt(a.getExpiresAt())
                .revokedAt(a.getRevokedAt())
                .revokedBy(a.getRevokedBy())
                .reason(a.getReason())
                .build());
    }

    @Override
    public List<UserPermissionAssignment> findPermissionAssignmentsByUserId(Long userId) {
        return userPermRepo.findByUserId(userId).stream()
                .map(e -> UserPermissionAssignment.builder()
                        .id(e.getId())
                        .userId(e.getUserId())
                        .permissionId(e.getPermissionId())
                        .effect(e.getEffect())
                        .assignedAt(e.getAssignedAt())
                        .assignedBy(e.getAssignedBy())
                        .expiresAt(e.getExpiresAt())
                        .revokedAt(e.getRevokedAt())
                        .revokedBy(e.getRevokedBy())
                        .reason(e.getReason())
                        .build())
                .toList();
    }

    @Override
    public Optional<UserPermissionAssignment> findPermissionAssignment(Long userId, Long permissionId) {
        return userPermRepo.findByUserIdAndPermissionId(userId, permissionId)
                .map(e -> UserPermissionAssignment.builder()
                        .id(e.getId())
                        .userId(e.getUserId())
                        .permissionId(e.getPermissionId())
                        .effect(e.getEffect())
                        .assignedAt(e.getAssignedAt())
                        .assignedBy(e.getAssignedBy())
                        .expiresAt(e.getExpiresAt())
                        .revokedAt(e.getRevokedAt())
                        .revokedBy(e.getRevokedBy())
                        .reason(e.getReason())
                        .build());
    }

    private UserJpaEntity mapToJpa(User u) {
        return UserJpaEntity.builder()
                .id(u.getId())
                .uuid(u.getUuid())
                .username(u.getUsername())
                .email(u.getEmail())
                .emailVerified(u.isEmailVerified())
                .passwordHash(u.getPasswordHash())
                .givenName(u.getGivenName())
                .familyName(u.getFamilyName())
                .middleName(u.getMiddleName())
                .nickname(u.getNickname())
                .gender(u.getGender())
                .birthdate(u.getBirthdate())
                .profileUrl(u.getProfileUrl())
                .pictureUrl(u.getPictureUrl())
                .websiteUrl(u.getWebsiteUrl())
                .zoneinfo(u.getZoneinfo())
                .locale(u.getLocale())
                .phoneNumber(u.getPhoneNumber())
                .phoneNumberVerified(u.isPhoneNumberVerified())
                .addressStreetAddress(u.getAddressStreetAddress())
                .addressLocality(u.getAddressLocality())
                .addressRegion(u.getAddressRegion())
                .addressPostalCode(u.getAddressPostalCode())
                .addressCountry(u.getAddressCountry())
                .enabled(u.isEnabled())
                .accountNonExpired(u.isAccountNonExpired())
                .accountNonLocked(u.isAccountNonLocked())
                .credentialsNonExpired(u.isCredentialsNonExpired())
                .failedLoginAttempts(u.getFailedLoginAttempts())
                .lastLoginAt(u.getLastLoginAt())
                .passwordChangedAt(u.getPasswordChangedAt())
                .isDeleted(u.isDeleted())
                .deletedAt(u.getDeletedAt())
                .deletedBy(u.getDeletedBy())
                .createdAt(u.getCreatedAt())
                .createdBy(u.getCreatedBy())
                .updatedAt(u.getUpdatedAt())
                .updatedBy(u.getUpdatedBy())
                .version(u.getVersion() != null ? u.getVersion() : 0L)
                .build();
    }

    private User mapToDomain(UserJpaEntity e) {
        return User.builder()
                .id(e.getId())
                .uuid(e.getUuid())
                .username(e.getUsername())
                .email(e.getEmail())
                .emailVerified(e.isEmailVerified())
                .passwordHash(e.getPasswordHash())
                .givenName(e.getGivenName())
                .familyName(e.getFamilyName())
                .middleName(e.getMiddleName())
                .nickname(e.getNickname())
                .gender(e.getGender())
                .birthdate(e.getBirthdate())
                .profileUrl(e.getProfileUrl())
                .pictureUrl(e.getPictureUrl())
                .websiteUrl(e.getWebsiteUrl())
                .zoneinfo(e.getZoneinfo())
                .locale(e.getLocale())
                .phoneNumber(e.getPhoneNumber())
                .phoneNumberVerified(e.isPhoneNumberVerified())
                .addressStreetAddress(e.getAddressStreetAddress())
                .addressLocality(e.getAddressLocality())
                .addressRegion(e.getAddressRegion())
                .addressPostalCode(e.getAddressPostalCode())
                .addressCountry(e.getAddressCountry())
                .enabled(e.isEnabled())
                .accountNonExpired(e.isAccountNonExpired())
                .accountNonLocked(e.isAccountNonLocked())
                .credentialsNonExpired(e.isCredentialsNonExpired())
                .failedLoginAttempts(e.getFailedLoginAttempts())
                .lastLoginAt(e.getLastLoginAt())
                .passwordChangedAt(e.getPasswordChangedAt())
                .isDeleted(e.isDeleted())
                .deletedAt(e.getDeletedAt())
                .deletedBy(e.getDeletedBy())
                .createdAt(e.getCreatedAt())
                .createdBy(e.getCreatedBy())
                .updatedAt(e.getUpdatedAt())
                .updatedBy(e.getUpdatedBy())
                .version(e.getVersion())
                .build();
    }
}