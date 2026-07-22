package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Feature.Customer.Dto.BookingResponse;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerMyTicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * API quan ly ve cua khach hang (E4).
 * - GET /api/customer/bookings?accountId=...        : danh sach ve cua toi
 * - GET /api/customer/bookings/{bookingId}?accountId=... : chi tiet mot don
 *
 * accountId tam truyen qua query cho den khi co Authentication (Quan).
 */
@RestController
@RequestMapping("/api/customer/bookings")
public class CustomerMyTicketController {

    private final CustomerMyTicketService myTicketService;

    public CustomerMyTicketController(CustomerMyTicketService myTicketService) {
        this.myTicketService = myTicketService;
    }

    @GetMapping
    public List<BookingResponse> myBookings(@RequestParam UUID accountId) {
        return myTicketService.listMyBookings(accountId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse bookingDetail(@PathVariable UUID bookingId, @RequestParam UUID accountId) {
        return myTicketService.getMyBooking(bookingId, accountId);
    }
}
