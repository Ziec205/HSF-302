package group.edu.bus_ticket.Feature.Customer.Dto;

import group.edu.bus_ticket.Domain.Enum.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Ket qua tra ve sau khi tao / xem mot don dat ve.
 */
public record BookingResponse(
        UUID bookingId,
        BookingStatus status,
        Double totalPrice,
        LocalDateTime dateBooked,
        LocalDateTime holdExpiresAt,
        UUID tripId,
        String destinationFrom,
        String destinationTo,
        LocalDateTime departureTime,
        List<TicketDto> tickets
) {
}
