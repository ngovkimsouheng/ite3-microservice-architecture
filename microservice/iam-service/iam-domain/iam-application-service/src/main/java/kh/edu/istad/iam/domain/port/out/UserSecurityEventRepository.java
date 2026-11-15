package kh.edu.istad.iam.domain.port.out;

import kh.edu.istad.iam.domain.core.entity.UserSecurityEvent;

import java.util.List;

public interface UserSecurityEventRepository {
    UserSecurityEvent save(UserSecurityEvent event);
    List<UserSecurityEvent> findUserLoginHistory(Long userId, String status, int limit);
    List<UserSecurityEvent> findUserLoginHistoryByUsername(String username, String status, int limit);
    List<UserSecurityEvent> findUserActivities(Long userId, int limit);
    List<UserSecurityEvent> findUserActivitiesByUsername(String username, int limit);
}
