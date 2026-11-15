package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.UserSecurityEventJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSecurityEventJpaRepository extends JpaRepository<UserSecurityEventJpaEntity, Long> {

    @Query("SELECT e FROM UserSecurityEventJpaEntity e WHERE ((:userId IS NOT NULL AND e.userId = :userId) OR (e.username IS NOT NULL AND LOWER(e.username) = LOWER(:username))) AND e.category = 'LOGIN' ORDER BY e.createdAt DESC")
    List<UserSecurityEventJpaEntity> findLoginHistoryAll(@Param("userId") Long userId, @Param("username") String username, Pageable pageable);

    @Query("SELECT e FROM UserSecurityEventJpaEntity e WHERE ((:userId IS NOT NULL AND e.userId = :userId) OR (e.username IS NOT NULL AND LOWER(e.username) = LOWER(:username))) AND e.category = 'LOGIN' AND LOWER(e.status) = LOWER(:status) ORDER BY e.createdAt DESC")
    List<UserSecurityEventJpaEntity> findLoginHistoryByStatus(@Param("userId") Long userId, @Param("username") String username, @Param("status") String status, Pageable pageable);

    @Query("SELECT e FROM UserSecurityEventJpaEntity e WHERE ((:userId IS NOT NULL AND e.userId = :userId) OR (e.username IS NOT NULL AND LOWER(e.username) = LOWER(:username))) AND e.category = 'ACTIVITY' ORDER BY e.createdAt DESC")
    List<UserSecurityEventJpaEntity> findActivities(@Param("userId") Long userId, @Param("username") String username, Pageable pageable);

    // --- System-wide Queries for Enterprise Admin Audit ---
    @Query("SELECT e FROM UserSecurityEventJpaEntity e WHERE e.category = 'LOGIN' ORDER BY e.createdAt DESC")
    List<UserSecurityEventJpaEntity> findAllSystemLoginHistory(Pageable pageable);

    @Query("SELECT e FROM UserSecurityEventJpaEntity e WHERE e.category = 'LOGIN' AND LOWER(e.status) = LOWER(:status) ORDER BY e.createdAt DESC")
    List<UserSecurityEventJpaEntity> findAllSystemLoginHistoryByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT e FROM UserSecurityEventJpaEntity e WHERE e.category = 'ACTIVITY' ORDER BY e.createdAt DESC")
    List<UserSecurityEventJpaEntity> findAllSystemActivities(Pageable pageable);
}
