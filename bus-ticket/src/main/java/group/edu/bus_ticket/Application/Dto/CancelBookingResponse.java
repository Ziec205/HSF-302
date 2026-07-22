package group.edu.bus_ticket.Application.Dto;

import group.edu.bus_ticket.Domain.Enum.BookingStatus;

import java.util.UUID;

/**
 * Ket qua huy ve (E5): trang thai moi + so tien duoc hoan theo chinh sach.
 * Viec chuyen tien hoan thuc su thuoc module Payment (lam sau).
 */
public record CancelBookingResponse(
        UUID bookingId,
        BookingStatus status,
        Double refundAmount,
        String message
) {
}
