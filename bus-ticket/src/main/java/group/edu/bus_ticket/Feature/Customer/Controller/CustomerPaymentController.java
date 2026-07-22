package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Feature.Customer.Dto.BookingResponse;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerPaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API thanh toan (E3 - PLACEHOLDER).
 * POST /api/customer/bookings/{bookingId}/pay?accountId=...
 *
 * Se duoc thay the bang module Payment that (cong thanh toan, idempotency).
 */
@RestController
@RequestMapping("/api/customer/bookings")
public class CustomerPaymentController {

    private final CustomerPaymentService paymentService;

    public CustomerPaymentController(CustomerPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{bookingId}/pay")
    public BookingResponse pay(@PathVariable UUID bookingId, @RequestParam UUID accountId) {
        return paymentService.confirmPayment(bookingId, accountId);
    }
}
