package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.SystemConfigurationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemConfigurationJpaRepository extends JpaRepository<SystemConfigurationJpaEntity, Long> {
}
