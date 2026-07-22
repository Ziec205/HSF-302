package group.edu.bus_ticket.Feature.Customer.Service;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Domain.Enum.SeatStatus;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Nghiep vu giai phong ghe (cross-cutting F: "Giu ghe tam & timeout").
 *
 * Dung chung cho:
 * - Huy don thu cong (E5): tra ghe ve AVAILABLE.
 * - Job dinh ky: don PENDING_PAYMENT qua han giu ghe -> EXPIRED + tra ghe.
 */
@Service
public class SeatReleaseService {

    private static final Logger log = LoggerFactory.getLogger(SeatReleaseService.class);

    private final BookingJpaRepo bookingRepo;
    private final SeatAvailabilityJpaRepo seatRepo;

    public SeatReleaseService(BookingJpaRepo bookingRepo, SeatAvailabilityJpaRepo seatRepo) {
        this.bookingRepo = bookingRepo;
        this.seatRepo = seatRepo;
    }

    /** Tra tat ca ghe cua mot don ve trang thai AVAILABLE (dung khi huy). */
    public void releaseSeatsOfBooking(Booking booking) {
        List<SeatAvailability> seats = seatRepo.findByBookingDetail_Booking_Id(booking.getId());
        for (SeatAvailability seat : seats) {
            freeSeat(seat);
        }
    }

    private void freeSeat(SeatAvailability seat) {
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setHeldUntil(null);
        seat.setBookingDetail(null);
        seatRepo.save(seat);
    }

    /**
     * Giai phong cac don giu ghe qua han: chuyen sang EXPIRED va tra ghe.
     * Tra ve so don da xu ly.
     */
    @Transactional
    public int releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expired = bookingRepo.findByStatusAndHoldExpiresAtBefore(BookingStatus.PENDING_PAYMENT, now);
        for (Booking booking : expired) {
            releaseSeatsOfBooking(booking);
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepo.save(booking);
        }

        // Doi soat them: ghe con ket o trang thai HELD nhung da qua han.
        for (SeatAvailability seat : seatRepo.findExpiredHolds(now)) {
            freeSeat(seat);
        }

        if (!expired.isEmpty()) {
            log.info("Da giai phong {} don giu ghe qua han", expired.size());
        }
        return expired.size();
    }
}
