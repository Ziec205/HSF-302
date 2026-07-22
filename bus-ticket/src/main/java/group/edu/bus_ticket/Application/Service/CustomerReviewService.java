package group.edu.bus_ticket.Application.Service;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.Review;
import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Entity.Trip;
import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Application.Dto.ReviewRequest;
import group.edu.bus_ticket.Application.Dto.ReviewResponse;
import group.edu.bus_ticket.Infrastructure.Persistence.Review.ReviewJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Chuc nang E6 - Danh gia & phan hoi sau chuyen di.
 *
 * Ngoai le:
 * - Danh gia khi chua hoan thanh chuyen -> chan (dung moc: chuyen chua khoi hanh xong).
 * - Danh gia trung cho cung 1 booking -> chan.
 * - Don da huy -> khong cho danh gia.
 */
@Service
public class CustomerReviewService {

    private final ReviewJpaRepo reviewRepo;
    private final SeatAvailabilityJpaRepo seatRepo;
    private final CustomerMyTicketService myTicketService;

    public CustomerReviewService(ReviewJpaRepo reviewRepo,
                                 SeatAvailabilityJpaRepo seatRepo,
                                 CustomerMyTicketService myTicketService) {
        this.reviewRepo = reviewRepo;
        this.seatRepo = seatRepo;
        this.myTicketService = myTicketService;
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest req) {
        Booking booking = myTicketService.loadOwnedBooking(req.bookingId(), req.accountId());

        BookingStatus status = booking.getStatus();
        if (status == BookingStatus.CANCELLED || status == BookingStatus.EXPIRED
                || status == BookingStatus.REFUNDED || status == BookingStatus.REFUND_PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Don da huy/hoan, khong the danh gia");
        }

        if (reviewRepo.existsByBooking_Id(req.bookingId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Don nay da duoc danh gia");
        }

        Trip trip = findTrip(req.bookingId());
        if (trip == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khong xac dinh duoc chuyen di de danh gia");
        }
        // Chi cho danh gia sau khi chuyen da khoi hanh (coi nhu da hoan thanh).
        if (trip.getDepartureTime() == null || trip.getDepartureTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chua the danh gia khi chuyen chua hoan thanh");
        }

        Review review = new Review();
        review.setAccount(booking.getAccount());
        review.setBooking(booking);
        review.setTrip(trip);
        review.setRating(req.rating());
        review.setComment(req.comment());
        review.setCreatedAt(LocalDateTime.now());
        review = reviewRepo.save(review);

        return new ReviewResponse(review.getId(), trip.getId(), review.getRating(),
                review.getComment(), review.getCreatedAt());
    }

    private Trip findTrip(java.util.UUID bookingId) {
        List<SeatAvailability> seats = seatRepo.findByBookingDetail_Booking_Id(bookingId);
        return seats.stream()
                .map(SeatAvailability::getTrip)
                .filter(t -> t != null)
                .findFirst()
                .orElse(null);
    }
}
