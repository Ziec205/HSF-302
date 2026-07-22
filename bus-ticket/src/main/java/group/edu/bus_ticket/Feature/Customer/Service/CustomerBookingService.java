package group.edu.bus_ticket.Feature.Customer.Service;

import group.edu.bus_ticket.Domain.Entity.Account;
import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.BookingDetail;
import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import group.edu.bus_ticket.Domain.Entity.Trip;
import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Domain.Enum.SeatStatus;
import group.edu.bus_ticket.Domain.Enum.Status;
import group.edu.bus_ticket.Feature.Customer.Dto.BookingResponse;
import group.edu.bus_ticket.Feature.Customer.Dto.CreateBookingRequest;
import group.edu.bus_ticket.Feature.Customer.Dto.PassengerRequest;
import group.edu.bus_ticket.Feature.Customer.Dto.SeatDto;
import group.edu.bus_ticket.Infrastructure.Persistence.Account.AccountJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingDetailJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Trip.TripJpaRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Chuc nang E2 - Chon ghe & dat ve (giu ghe tam co timeout).
 *
 * Xu ly cac ngoai le trong FunctionList:
 * - Ghe vua bi nguoi khac chon (race condition) -> khoa bi quan + bao 409.
 * - Vuot so ghe toi da cho 1 lan dat -> chan.
 * - Chuyen da khoi hanh / khong con ban -> chan.
 */
@Service
public class CustomerBookingService {

    private static final int MAX_SEATS_PER_BOOKING = 5;

    private final AccountJpaRepo accountRepo;
    private final TripJpaRepo tripRepo;
    private final SeatAvailabilityJpaRepo seatRepo;
    private final BookingJpaRepo bookingRepo;
    private final BookingDetailJpaRepo bookingDetailRepo;
    private final PricingService pricingService;
    private final BookingAssembler assembler;

    @Value("${app.booking.seat-hold-minutes:10}")
    private long seatHoldMinutes;

    public CustomerBookingService(AccountJpaRepo accountRepo,
                                  TripJpaRepo tripRepo,
                                  SeatAvailabilityJpaRepo seatRepo,
                                  BookingJpaRepo bookingRepo,
                                  BookingDetailJpaRepo bookingDetailRepo,
                                  PricingService pricingService,
                                  BookingAssembler assembler) {
        this.accountRepo = accountRepo;
        this.tripRepo = tripRepo;
        this.seatRepo = seatRepo;
        this.bookingRepo = bookingRepo;
        this.bookingDetailRepo = bookingDetailRepo;
        this.pricingService = pricingService;
        this.assembler = assembler;
    }

    /** So do ghe cua mot chuyen (E2). */
    public List<SeatDto> getSeatMap(UUID tripId) {
        tripRepo.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay chuyen"));
        LocalDateTime now = LocalDateTime.now();
        return seatRepo.findByTrip_IdOrderBySeatCode(tripId).stream()
                .map(s -> new SeatDto(s.getSeatCode(), s.getStatus(), CustomerTripService.isSeatFree(s, now)))
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest req) {
        Account account = accountRepo.findById(req.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay tai khoan"));
        Trip trip = tripRepo.findById(req.tripId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay chuyen"));

        LocalDateTime now = LocalDateTime.now();
        if (trip.getStatus() != Status.AVAILABLE
                || (trip.getDepartureTime() != null && !trip.getDepartureTime().isAfter(now))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chuyen da khoi hanh hoac khong con ban ve");
        }

        List<PassengerRequest> passengers = req.passengers();
        if (passengers.size() > MAX_SEATS_PER_BOOKING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Toi da " + MAX_SEATS_PER_BOOKING + " ghe cho mot lan dat");
        }

        // Chan chon trung ma ghe trong cung yeu cau.
        List<String> codes = passengers.stream().map(PassengerRequest::seatCode).collect(Collectors.toList());
        Set<String> uniqueCodes = new HashSet<>(codes);
        if (uniqueCodes.size() != codes.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sach ghe bi trung");
        }

        // Khoa bi quan cac ghe -> tranh 2 nguoi dat trung 1 ghe.
        Map<String, SeatAvailability> seatByCode = seatRepo.lockSeats(req.tripId(), codes).stream()
                .collect(Collectors.toMap(SeatAvailability::getSeatCode, s -> s, (a, b) -> a));

        for (String code : codes) {
            SeatAvailability seat = seatByCode.get(code);
            if (seat == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ghe " + code + " khong ton tai tren chuyen");
            }
            if (!CustomerTripService.isSeatFree(seat, now)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ghe " + code + " khong con trong");
            }
        }

        LocalDateTime holdExpiresAt = now.plusMinutes(seatHoldMinutes);

        Booking booking = new Booking();
        booking.setDateBooked(now);
        booking.setBookingType(req.bookingType());
        booking.setNote(req.note());
        booking.setAccount(account);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setHoldExpiresAt(holdExpiresAt);
        booking.setTotalPrice(0.0);
        booking = bookingRepo.save(booking);

        double total = 0.0;
        List<BookingDetail> details = new ArrayList<>();
        for (PassengerRequest p : passengers) {
            SeatAvailability seat = seatByCode.get(p.seatCode());

            double ticketPrice = pricingService.calcTicketPrice(trip, seat.getStartStationOrder(), seat.getEndStationOrder());
            double luggageFee = pricingService.calcLuggageFee(p.luggageWeightKg());
            double subTotal = ticketPrice + luggageFee;

            BookingDetail detail = new BookingDetail();
            detail.setPassengerName(p.passengerName());
            detail.setTicketPrice(ticketPrice);
            detail.setLuggageWeightKg(p.luggageWeightKg() != null ? p.luggageWeightKg() : 0.0);
            detail.setLuggageFee(luggageFee);
            detail.setSubTotal(subTotal);
            detail.setReturnTicket(false);
            detail.setBooking(booking);
            detail = bookingDetailRepo.save(detail);
            details.add(detail);

            // Giu ghe tam.
            seat.setStatus(SeatStatus.HELD);
            seat.setHeldUntil(holdExpiresAt);
            seat.setBookingDetail(detail);
            seatRepo.save(seat);

            total += subTotal;
        }

        booking.setTotalPrice(total);
        booking = bookingRepo.save(booking);

        return assembler.toResponse(booking, details);
    }
}
