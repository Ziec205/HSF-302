package group.edu.bus_ticket.Feature.Customer.Service;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.BookingDetail;
import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Domain.Enum.SeatStatus;
import group.edu.bus_ticket.Feature.Customer.Dto.BookingResponse;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingDetailJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Chuc nang E3 - Thanh toan (PLACEHOLDER).
 *
 * Day chi la ban tam de HOAN TAT luong dat ve: xac nhan don PENDING_PAYMENT ->
 * CONFIRMED va chuyen ghe HELD -> BOOKED. Cong thanh toan that (Payment/Transaction,
 * idempotency, doi soat) se do module Payment (Viet lam sau) thay the.
 */
@Service
public class CustomerPaymentService {

    private final BookingJpaRepo bookingRepo;
    private final BookingDetailJpaRepo bookingDetailRepo;
    private final SeatAvailabilityJpaRepo seatRepo;
    private final CustomerMyTicketService myTicketService;
    private final BookingAssembler assembler;

    public CustomerPaymentService(BookingJpaRepo bookingRepo,
                                  BookingDetailJpaRepo bookingDetailRepo,
                                  SeatAvailabilityJpaRepo seatRepo,
                                  CustomerMyTicketService myTicketService,
                                  BookingAssembler assembler) {
        this.bookingRepo = bookingRepo;
        this.bookingDetailRepo = bookingDetailRepo;
        this.seatRepo = seatRepo;
        this.myTicketService = myTicketService;
        this.assembler = assembler;
    }

    @Transactional
    public BookingResponse confirmPayment(UUID bookingId, UUID accountId) {
        Booking booking = myTicketService.loadOwnedBooking(bookingId, accountId);

        // Idempotent: da thanh toan roi thi tra ve luon.
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return assembler.toResponse(booking, bookingDetailRepo.findByBooking_Id(bookingId));
        }
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don khong o trang thai cho thanh toan");
        }
        if (booking.getHoldExpiresAt() != null
                && booking.getHoldExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Het thoi gian giu ghe, vui long dat lai");
        }

        // Chot ghe da giu -> da dat.
        List<SeatAvailability> seats = seatRepo.findByBookingDetail_Booking_Id(bookingId);
        for (SeatAvailability seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setHeldUntil(null);
            seatRepo.save(seat);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setHoldExpiresAt(null);
        booking = bookingRepo.save(booking);

        List<BookingDetail> details = bookingDetailRepo.findByBooking_Id(bookingId);
        return assembler.toResponse(booking, details);
    }
}
