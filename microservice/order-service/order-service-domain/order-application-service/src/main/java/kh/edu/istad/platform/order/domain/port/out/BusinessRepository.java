package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.platform.order.domain.entity.Business;

import java.util.Optional;

public interface BusinessRepository {
    Optional<Business> findBusinessInformation(Business business);
}