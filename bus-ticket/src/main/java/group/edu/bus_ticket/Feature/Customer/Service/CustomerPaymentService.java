package group.edu.bus_ticket.Feature.Customer.Service;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.Payment;
import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Entity.Transaction;
import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Domain.Enum.PaymentMethod;
import group.edu.bus_ticket.Domain.Enum.PaymentStatus;
import group.edu.bus_ticket.Domain.Enum.SeatStatus;
import group.edu.bus_ticket.Domain.Enum.TransactionStatus;
import group.edu.bus_ticket.Feature.Customer.Dto.PaymentResponse;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Payment.PaymentJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Transaction.TransactionJpaRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Chuc nang E3 - Thanh toan (GIA LAP cong thanh toan).
 *
 * Mo phong mot cong thanh toan luon thanh cong:
 *  - Tao ban ghi Payment (so tien, phuong thuc, ma giao dich, trang thai PAID)
 *  - Tao ban ghi Transaction (from = khach, status PAID) mo phong dong tien
 *  - Chot ghe HELD -> BOOKED, don PENDING_PAYMENT -> CONFIRMED
 *
 * Dam bao idempotency: goi lai khi don da CONFIRMED se tra ve payment da co,
 * khong tao trung (F: idempotency thanh toan).
 */
@Service
public class CustomerPaymentService {

    private final BookingJpaRepo bookingRepo;
    private final SeatAvailabilityJpaRepo seatRepo;
    private final PaymentJpaRepo paymentRepo;
    private final TransactionJpaRepo transactionRepo;
    private final CustomerMyTicketService myTicketService;

    public CustomerPaymentService(BookingJpaRepo bookingRepo,
                                  SeatAvailabilityJpaRepo seatRepo,
                                  PaymentJpaRepo paymentRepo,
                                  TransactionJpaRepo transactionRepo,
                                  CustomerMyTicketService myTicketService) {
        this.bookingRepo = bookingRepo;
        this.seatRepo = seatRepo;
        this.paymentRepo = paymentRepo;
        this.transactionRepo = transactionRepo;
        this.myTicketService = myTicketService;
    }

    @Transactional
    public PaymentResponse pay(UUID bookingId, UUID accountId, PaymentMethod method) {
        Booking booking = myTicketService.loadOwnedBooking(bookingId, accountId);
        PaymentMethod payMethod = method != null ? method : PaymentMethod.CASH;

        // Idempotent: don da thanh toan -> tra ve payment da co.
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            Payment existing = paymentRepo.findByBooking_Id(bookingId).orElse(null);
            return existing != null ? toResponse(existing, booking)
                    : new PaymentResponse(null, bookingId, null, booking.getTotalPrice(),
                    payMethod, PaymentStatus.PAID, booking.getStatus(), null);
        }
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don khong o trang thai cho thanh toan");
        }
        if (booking.getHoldExpiresAt() != null && booking.getHoldExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Het thoi gian giu ghe, vui long dat lai");
        }

        LocalDateTime now = LocalDateTime.now();

        // --- Gia lap cong thanh toan: luon thanh cong ---
        Payment payment = new Payment(now, payMethod, booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionCode(generateTransactionCode());
        payment.setPaidAt(now);
        payment = paymentRepo.save(payment);

        Transaction txn = new Transaction(booking.getAccount(), null, TransactionStatus.PAID, payment);
        transactionRepo.save(txn);

        // Chot ghe da giu -> da dat.
        List<SeatAvailability> seats = seatRepo.findByBookingDetail_Booking_Id(bookingId);
        for (SeatAvailability seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setHeldUntil(null);
            seatRepo.save(seat);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setHoldExpiresAt(null);
        bookingRepo.save(booking);

        return toResponse(payment, booking);
    }

    private PaymentResponse toResponse(Payment p, Booking booking) {
        return new PaymentResponse(p.getId(), booking.getId(), p.getTransactionCode(),
                p.getAmount(), p.getPaymentMethod(), p.getStatus(), booking.getStatus(), p.getPaidAt());
    }

    /** Ma giao dich gia lap, vi du: SIMPAY-3F9A1C7B */
    private String generateTransactionCode() {
        return "SIMPAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
