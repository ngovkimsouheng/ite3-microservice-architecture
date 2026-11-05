package kh.edu.istad.iam.feature.role;

import kh.edu.istad.iam.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Role_JpaRepository extends JpaRepository<Role, Long> {
}