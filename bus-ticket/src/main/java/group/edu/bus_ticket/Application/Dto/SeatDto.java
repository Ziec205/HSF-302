package group.edu.bus_ticket.Application.Dto;

import group.edu.bus_ticket.Domain.Enum.SeatStatus;

/**
 * Mot ghe trong so do ghe cua chuyen (E2).
 * free = true nghia la khach co the chon (AVAILABLE hoac ghe HELD da qua han giu).
 */
public record SeatDto(
        String seatCode,
        SeatStatus status,
        boolean free
) {
}
