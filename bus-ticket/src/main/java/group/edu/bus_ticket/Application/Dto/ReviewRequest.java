package group.edu.bus_ticket.Application.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Yeu cau danh gia sau chuyen di (E6).
 */
public record ReviewRequest(
        @NotNull(message = "Thieu bookingId") UUID bookingId,
        @NotNull(message = "Thieu accountId") UUID accountId,
        @NotNull(message = "Vui long cham diem")
        @Min(value = 1, message = "Toi thieu 1 sao")
        @Max(value = 5, message = "Toi da 5 sao") Integer rating,
        @Size(max = 1000, message = "Nhan xet toi da 1000 ky tu") String comment
) {
}
