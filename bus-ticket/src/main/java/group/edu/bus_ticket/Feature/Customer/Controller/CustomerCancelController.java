package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Feature.Customer.Dto.CancelBookingResponse;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerCancelService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API huy ve / yeu cau hoan tien cho khach hang (E5).
 * POST /api/customer/bookings/{bookingId}/cancel?accountId=...
 */
@RestController
@RequestMapping("/api/customer/bookings")
public class CustomerCancelController {

    private final CustomerCancelService cancelService;

    public CustomerCancelController(CustomerCancelService cancelService) {
        this.cancelService = cancelService;
    }

    @PostMapping("/{bookingId}/cancel")
    public CancelBookingResponse cancel(@PathVariable UUID bookingId, @RequestParam UUID accountId) {
        return cancelService.cancel(bookingId, accountId);
    }
}
