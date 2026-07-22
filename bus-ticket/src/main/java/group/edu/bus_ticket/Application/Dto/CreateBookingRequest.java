package group.edu.bus_ticket.Application.Dto;

import group.edu.bus_ticket.Domain.Enum.BookingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Yeu cau tao don dat ve (E2).
 *
 * accountId tam thoi truyen tu client cho den khi module Authentication (Quan) hoan thien;
 * khi co JWT/session se lay tu nguoi dung dang dang nhap thay vi tin tuong client.
 */
public record CreateBookingRequest(
        @NotNull(message = "Thieu tripId") UUID tripId,
        @NotNull(message = "Thieu accountId") UUID accountId,
        @NotNull(message = "Thieu loai dat ve") BookingType bookingType,
        String note,
        @NotEmpty(message = "Phai chon it nhat 1 ghe")
        @Valid List<PassengerRequest> passengers
) {
}
