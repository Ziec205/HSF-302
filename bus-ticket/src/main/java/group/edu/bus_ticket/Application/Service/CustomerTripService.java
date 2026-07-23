package group.edu.bus_ticket.Application.Service;

import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Entity.Trip;
import group.edu.bus_ticket.Domain.Enum.SeatStatus;
import group.edu.bus_ticket.Domain.Enum.Status;
import group.edu.bus_ticket.Application.Dto.TripSearchResponse;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Trip.TripJpaRepo;
import org.springframework.stereotype.Service;
import group.edu.bus_ticket.Domain.Exception.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Chuc nang E1 - Tim kiem chuyen cho khach hang.
 *
 * Happy case: khach nhap diem di, diem den, ngay -> tra ve danh sach chuyen kha dung
 * kem so ghe trong.
 * Ngoai le: diem di = diem den -> loi; ngay trong qua khu -> chan.
 */
@Service
public class CustomerTripService {

    private final TripJpaRepo tripRepo;
    private final SeatAvailabilityJpaRepo seatRepo;

    public CustomerTripService(TripJpaRepo tripRepo, SeatAvailabilityJpaRepo seatRepo) {
        this.tripRepo = tripRepo;
        this.seatRepo = seatRepo;
    }

    public List<TripSearchResponse> searchTrips(String from, String to, LocalDate date) {
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new BusinessException("Vui long nhap diem di va diem den");
        }
        if (from.trim().equalsIgnoreCase(to.trim())) {
            throw new BusinessException("Diem di va diem den khong duoc trung nhau");
        }
        if (date == null) {
            throw new BusinessException("Vui long chon ngay di");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException("Ngay di khong duoc trong qua khu");
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        // Neu tim trong ngay hom nay thi chi lay cac chuyen chua khoi hanh.
        LocalDateTime now = LocalDateTime.now();
        if (start.isBefore(now)) {
            start = now;
        }

        List<Trip> trips = tripRepo.searchTrips(from.trim(), to.trim(), start, end, Status.AVAILABLE);

        return trips.stream()
                .map(t -> new TripSearchResponse(
                        t.getId(),
                        t.getDestinationFrom(),
                        t.getDestinationTo(),
                        t.getDepartureTime(),
                        t.getBus() != null ? t.getBus().getBusName() : null,
                        countAvailableSeats(t.getId(), now)))
                .toList();
    }

    private long countAvailableSeats(java.util.UUID tripId, LocalDateTime now) {
        return seatRepo.findByTrip_IdOrderBySeatCode(tripId).stream()
                .filter(s -> isSeatFree(s, now))
                .count();
    }

    /** Ghe con trong neu AVAILABLE, hoac dang HELD nhung da qua han giu. */
    static boolean isSeatFree(SeatAvailability seat, LocalDateTime now) {
        if (seat.getStatus() == SeatStatus.AVAILABLE) {
            return true;
        }
        return seat.getStatus() == SeatStatus.HELD
                && seat.getHeldUntil() != null
                && seat.getHeldUntil().isBefore(now);
    }
}
