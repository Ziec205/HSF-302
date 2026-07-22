package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Feature.Customer.CurrentCustomerService;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerMyTicketService;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerPaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Trang E3 - Thanh toan (Thymeleaf). Ban placeholder: xac nhan don -> CONFIRMED.
 */
@Controller
public class CustomerPaymentPageController {

    private final CustomerMyTicketService myTicketService;
    private final CustomerPaymentService paymentService;
    private final CurrentCustomerService currentCustomer;

    public CustomerPaymentPageController(CustomerMyTicketService myTicketService,
                                         CustomerPaymentService paymentService,
                                         CurrentCustomerService currentCustomer) {
        this.myTicketService = myTicketService;
        this.paymentService = paymentService;
        this.currentCustomer = currentCustomer;
    }

    @GetMapping("/customer/payment/{bookingId}")
    public String paymentPage(@PathVariable UUID bookingId, Model model) {
        model.addAttribute("booking", myTicketService.getMyBooking(bookingId, currentCustomer.getAccountId()));
        return "customer/payment";
    }

    @PostMapping("/customer/payment/{bookingId}")
    public String pay(@PathVariable UUID bookingId,
                      @RequestParam(required = false) String method,
                      Model model) {
        try {
            paymentService.confirmPayment(bookingId, currentCustomer.getAccountId());
            return "redirect:/customer/tickets/" + bookingId + "?paid=1";
        } catch (ResponseStatusException ex) {
            model.addAttribute("error", ex.getReason());
            model.addAttribute("booking", myTicketService.getMyBooking(bookingId, currentCustomer.getAccountId()));
            return "customer/payment";
        }
    }
}
