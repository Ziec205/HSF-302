package group.edu.bus_ticket.Feature.Customer.Service;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.BookingDetail;
import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Entity.Trip;
import group.edu.bus_ticket.Feature.Customer.Dto.BookingResponse;
import group.edu.bus_ticket.Feature.Customer.Dto.TicketDto;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Dung {@link BookingResponse} tu entity Booking, dung chung cho cac chuc nang
 * dat ve (E2), ve cua toi (E4), huy ve (E5).
 */
@Component
public class BookingAssembler {

    private final SeatAvailabilityJpaRepo seatRepo;

    public BookingAssembler(SeatAvailabilityJpaRepo seatRepo) {
        this.seatRepo = seatRepo;
    }

    public BookingResponse toResponse(Booking booking, List<BookingDetail> details) {
        List<SeatAvailability> seats = seatRepo.findByBookingDetail_Booking_Id(booking.getId());

        // Map: bookingDetailId -> ma ghe
        Map<UUID, String> seatByDetail = seats.stream()
                .filter(s -> s.getBookingDetail() != null)
                .collect(Collectors.toMap(s -> s.getBookingDetail().getId(),
                        SeatAvailability::getSeatCode, (a, b) -> a));

        Trip trip = seats.stream()
                .map(SeatAvailability::getTrip)
                .filter(t -> t != null)
                .findFirst()
                .orElse(null);

        List<TicketDto> tickets = details.stream()
                .map(d -> new TicketDto(
                        d.getId(),
                        seatByDetail.get(d.getId()),
                        d.getPassengerName(),
                        d.getTicketPrice(),
                        d.getLuggageFee(),
                        d.getSubTotal()))
                .collect(Collectors.toList());

        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getDateBooked(),
                booking.getHoldExpiresAt(),
                trip != null ? trip.getId() : null,
                trip != null ? trip.getDestinationFrom() : null,
                trip != null ? trip.getDestinationTo() : null,
                trip != null ? trip.getDepartureTime() : null,
                tickets);
    }
}
