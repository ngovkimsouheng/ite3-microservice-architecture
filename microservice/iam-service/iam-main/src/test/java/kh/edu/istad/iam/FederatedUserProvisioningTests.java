package kh.edu.istad.iam;

import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.iam.domain.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.usecase.identity.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.iam.domain.usecase.user.GetUserAuthoritiesUseCase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FederatedUserProvisioningTests {

    @Test
    void createsUserAndPersistsProviderIdentityOnFirstVerifiedLogin() {
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        GetUserAuthoritiesUseCase authoritiesUseCase = mock(GetUserAuthoritiesUseCase.class);
        FindOrProvisionFederatedUserUseCase useCase =
                new FindOrProvisionFederatedUserUseCase(
                        identityRepository, userRepository, roleRepository, authoritiesUseCase);
        UUID userUuid = UUID.randomUUID();

        when(identityRepository.findByProviderAndProviderSubject("GOOGLE", "google-subject"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("person")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(42L);
            return user;
        });
        when(identityRepository.save(any(Identity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(defaultUserRole()));
        when(authoritiesUseCase.execute(any(UUID.class)))
                .thenReturn(new UserAuthoritiesResult(userUuid, Set.of(), Set.of(), List.of()));

        useCase.execute(new FederatedIdentityCommand(
                "google",
                "google-subject",
                "person@example.com",
                true,
                "Person Example",
                null,
                "https://example.com/avatar.png",
                Map.of("sub", "google-subject")
        ));

        var userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("person@example.com", userCaptor.getValue().getEmail());
        var identityCaptor = org.mockito.ArgumentCaptor.forClass(Identity.class);
        verify(identityRepository).save(identityCaptor.capture());
        assertEquals(42L, identityCaptor.getValue().getUserId());
        assertEquals("GOOGLE", identityCaptor.getValue().getProvider());
        assertEquals("google-subject", identityCaptor.getValue().getProviderSubject());
        verify(userRepository).saveRoleAssignment(any());
    }

    @Test
    void linksVerifiedProviderToExistingVerifiedAccount() {
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        GetUserAuthoritiesUseCase authoritiesUseCase = mock(GetUserAuthoritiesUseCase.class);
        FindOrProvisionFederatedUserUseCase useCase =
                new FindOrProvisionFederatedUserUseCase(
                        identityRepository, userRepository, roleRepository, authoritiesUseCase);

        UUID userUuid = UUID.randomUUID();
        User existingUser = User.builder()
                .id(42L)
                .uuid(userUuid)
                .email("person@example.com")
                .emailVerified(true)
                .enabled(true)
                .build();
        when(identityRepository.findByProviderAndProviderSubject("GOOGLE", "google-subject"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(existingUser));
        when(identityRepository.findActiveByUserIdAndProvider(42L, "GOOGLE")).thenReturn(Optional.empty());
        when(identityRepository.save(any(Identity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authoritiesUseCase.execute(userUuid))
                .thenReturn(new UserAuthoritiesResult(userUuid, Set.of(), Set.of(), List.of()));

        var result = useCase.execute(new FederatedIdentityCommand(
                "google",
                "google-subject",
                " PERSON@example.com ",
                true,
                "Person Example",
                null,
                null,
                Map.of()
        ));

        assertEquals(userUuid, result.uuid());
        Identity linkedIdentity = captureLinkedIdentity(identityRepository);
        assertEquals(42L, linkedIdentity.getUserId());
        assertEquals("GOOGLE", linkedIdentity.getProvider());
        assertEquals("google-subject", linkedIdentity.getProviderSubject());
        assertEquals("person@example.com", linkedIdentity.getEmail());
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository).save(existingUser);
    }

    @Test
    void doesNotLinkProviderToExistingAccountWithUnverifiedEmail() {
        IdentityRepository identityRepository = mock(IdentityRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        FindOrProvisionFederatedUserUseCase useCase = new FindOrProvisionFederatedUserUseCase(
                identityRepository,
                userRepository,
                roleRepository,
                mock(GetUserAuthoritiesUseCase.class)
        );
        User existingUser = User.builder()
                .id(42L)
                .uuid(UUID.randomUUID())
                .email("person@example.com")
                .emailVerified(false)
                .enabled(true)
                .build();
        when(identityRepository.findByProviderAndProviderSubject("GOOGLE", "google-subject"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new FederatedIdentityCommand(
                "google",
                "google-subject",
                "person@example.com",
                true,
                "Person Example",
                null,
                null,
                Map.of()
        )));

        verify(identityRepository, never()).save(any(Identity.class));
    }

    private Identity captureLinkedIdentity(IdentityRepository identityRepository) {
        var identityCaptor = org.mockito.ArgumentCaptor.forClass(Identity.class);
        verify(identityRepository).save(identityCaptor.capture());
        return identityCaptor.getValue();
    }

    private Role defaultUserRole() {
        return Role.builder().id(1L).name("USER").isActive(true).isSystem(true).build();
    }
}
