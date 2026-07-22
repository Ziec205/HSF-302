package group.edu.bus_ticket.Application.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Thong tin mot hanh khach / mot ghe trong yeu cau dat ve (E2).
 */
public record PassengerRequest(
        @Pattern(regexp = "^[A-Z]\\d{2}$", message = "Ma ghe khong dung dinh dang (vi du A01)")
        String seatCode,

        @NotBlank(message = "Ten hanh khach khong duoc de trong")
        String passengerName,

        @PositiveOrZero(message = "Khoi luong hanh ly khong duoc am")
        Double luggageWeightKg
) {
}
