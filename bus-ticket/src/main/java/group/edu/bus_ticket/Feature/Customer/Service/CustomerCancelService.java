package group.edu.bus_ticket.Feature.Customer.Service;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Feature.Customer.Dto.CancelBookingResponse;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Chuc nang E5 - Huy ve / yeu cau hoan tien.
 *
 * Chinh sach hoan (tinh theo thoi gian con lai truoc gio khoi hanh):
 * - >= 24 gio : hoan 100%
 * - >=  2 gio : hoan 50%
 * - <   2 gio : khong hoan (qua sat gio)
 * Chuyen da khoi hanh -> khong cho huy.
 *
 * Ghi chu: viec chuyen tien hoan thuc te do module Payment (Viet lam sau) xu ly;
 * o day chi chuyen trang thai sang REFUND_PENDING va tinh so tien du kien hoan.
 */
@Service
public class CustomerCancelService {

    private static final long FULL_REFUND_HOURS = 24;
    private static final long HALF_REFUND_HOURS = 2;

    private final BookingJpaRepo bookingRepo;
    private final SeatAvailabilityJpaRepo seatRepo;
    private final CustomerMyTicketService myTicketService;
    private final SeatReleaseService seatReleaseService;

    public CustomerCancelService(BookingJpaRepo bookingRepo,
                                 SeatAvailabilityJpaRepo seatRepo,
                                 CustomerMyTicketService myTicketService,
                                 SeatReleaseService seatReleaseService) {
        this.bookingRepo = bookingRepo;
        this.seatRepo = seatRepo;
        this.myTicketService = myTicketService;
        this.seatReleaseService = seatReleaseService;
    }

    @Transactional
    public CancelBookingResponse cancel(UUID bookingId, UUID accountId) {
        Booking booking = myTicketService.loadOwnedBooking(bookingId, accountId);

        BookingStatus status = booking.getStatus();
        if (status == BookingStatus.CANCELLED || status == BookingStatus.EXPIRED
                || status == BookingStatus.REFUNDED || status == BookingStatus.REFUND_PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don dat ve nay da bi huy hoac dang xu ly hoan tien");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = findDepartureTime(bookingId);
        if (departure != null && !departure.isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chuyen da khoi hanh, khong the huy ve");
        }

        // Tra ghe ve trang thai trong.
        seatReleaseService.releaseSeatsOfBooking(booking);

        // Don chua thanh toan -> huy, khong phat sinh hoan tien.
        if (status == BookingStatus.PENDING_PAYMENT) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepo.save(booking);
            return new CancelBookingResponse(bookingId, booking.getStatus(), 0.0,
                    "Da huy don chua thanh toan va giai phong ghe");
        }

        // Don da thanh toan (CONFIRMED) -> tinh hoan tien theo chinh sach.
        double rate = refundRate(now, departure);
        double total = booking.getTotalPrice() != null ? booking.getTotalPrice() : 0.0;
        double refund = total * rate;

        if (refund > 0) {
            booking.setStatus(BookingStatus.REFUND_PENDING);
            bookingRepo.save(booking);
            return new CancelBookingResponse(bookingId, booking.getStatus(), refund,
                    "Da huy ve, cho hoan " + (long) (rate * 100) + "% (" + refund + ")");
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepo.save(booking);
            return new CancelBookingResponse(bookingId, booking.getStatus(), 0.0,
                    "Huy qua sat gio khoi hanh, khong duoc hoan tien");
        }
    }

    private double refundRate(LocalDateTime now, LocalDateTime departure) {
        if (departure == null) {
            return 1.0; // khong xac dinh duoc gio -> hoan 100% cho khach
        }
        long hours = Duration.between(now, departure).toHours();
        if (hours >= FULL_REFUND_HOURS) {
            return 1.0;
        }
        if (hours >= HALF_REFUND_HOURS) {
            return 0.5;
        }
        return 0.0;
    }

    private LocalDateTime findDepartureTime(UUID bookingId) {
        List<SeatAvailability> seats = seatRepo.findByBookingDetail_Booking_Id(bookingId);
        return seats.stream()
                .map(SeatAvailability::getTrip)
                .filter(t -> t != null && t.getDepartureTime() != null)
                .map(t -> t.getDepartureTime())
                .findFirst()
                .orElse(null);
    }
}
