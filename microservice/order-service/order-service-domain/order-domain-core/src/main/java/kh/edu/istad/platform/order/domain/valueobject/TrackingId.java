package kh.edu.istad.platform.order.domain.valueobject;

import java.util.UUID;

public record TrackingId(UUID value) {

    public TrackingId {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Tracking ID cannot be null"
            );
        }
    }

    public static TrackingId generate() {
        return new TrackingId(UUID.randomUUID());
    }
}