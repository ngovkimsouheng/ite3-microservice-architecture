package kh.edu.istad.iam.domain.port.out;

import kh.edu.istad.iam.domain.core.entity.SystemConfiguration;

public interface SystemConfigurationRepository {
    SystemConfiguration getConfiguration();
    SystemConfiguration saveConfiguration(SystemConfiguration configuration);
}
