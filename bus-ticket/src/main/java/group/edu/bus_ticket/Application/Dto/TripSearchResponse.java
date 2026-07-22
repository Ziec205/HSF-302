package group.edu.bus_ticket.Application.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ket qua tra ve cho moi chuyen trong danh sach tim kiem (E1).
 */
public record TripSearchResponse(
        UUID tripId,
        String destinationFrom,
        String destinationTo,
        LocalDateTime departureTime,
        String busName,
        long availableSeats
) {
}
