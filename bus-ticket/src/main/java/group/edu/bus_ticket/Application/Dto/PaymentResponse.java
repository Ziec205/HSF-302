package group.edu.bus_ticket.Application.Dto;

import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Domain.Enum.PaymentMethod;
import group.edu.bus_ticket.Domain.Enum.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ket qua thanh toan (E3) - gia lap cong thanh toan.
 */
public record PaymentResponse(
        UUID paymentId,
        UUID bookingId,
        String transactionCode,
        Double amount,
        PaymentMethod method,
        PaymentStatus paymentStatus,
        BookingStatus bookingStatus,
        LocalDateTime paidAt
) {
}
