package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Domain.Enum.PaymentMethod;
import group.edu.bus_ticket.Feature.Customer.Dto.PaymentResponse;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerPaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API thanh toan (E3) - gia lap cong thanh toan.
 * POST /api/customer/bookings/{bookingId}/pay?accountId=...&method=CASH
 */
@RestController
@RequestMapping("/api/customer/bookings")
public class CustomerPaymentController {

    private final CustomerPaymentService paymentService;

    public CustomerPaymentController(CustomerPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{bookingId}/pay")
    public PaymentResponse pay(@PathVariable UUID bookingId,
                               @RequestParam UUID accountId,
                               @RequestParam(required = false) PaymentMethod method) {
        return paymentService.pay(bookingId, accountId, method);
    }
}
