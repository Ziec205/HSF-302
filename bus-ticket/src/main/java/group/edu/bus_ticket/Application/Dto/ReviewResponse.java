package group.edu.bus_ticket.Application.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ket qua danh gia (E6).
 */
public record ReviewResponse(
        UUID reviewId,
        UUID tripId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
