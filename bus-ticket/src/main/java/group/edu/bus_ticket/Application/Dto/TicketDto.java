package group.edu.bus_ticket.Application.Dto;

import java.util.UUID;

/**
 * Mot ve (booking detail) trong don dat ve.
 */
public record TicketDto(
        UUID bookingDetailId,
        String seatCode,
        String passengerName,
        Double ticketPrice,
        Double luggageFee,
        Double subTotal
) {
}
