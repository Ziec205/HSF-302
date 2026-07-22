package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Feature.Customer.Dto.BookingResponse;
import group.edu.bus_ticket.Feature.Customer.Dto.CreateBookingRequest;
import group.edu.bus_ticket.Feature.Customer.Dto.SeatDto;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerBookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API dat ve cho web khach hang (E2).
 * - GET  /api/customer/trips/{tripId}/seats  : xem so do ghe
 * - POST /api/customer/bookings              : tao don dat ve (giu ghe tam)
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerBookingController {

    private final CustomerBookingService bookingService;

    public CustomerBookingController(CustomerBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/trips/{tripId}/seats")
    public List<SeatDto> seatMap(@PathVariable UUID tripId) {
        return bookingService.getSeatMap(tripId);
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }
}
