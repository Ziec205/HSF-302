package group.edu.bus_ticket.Presentation.Controller;

import group.edu.bus_ticket.Domain.Enum.PaymentMethod;
import group.edu.bus_ticket.Application.Service.CurrentCustomerService;
import group.edu.bus_ticket.Application.Dto.PaymentResponse;
import group.edu.bus_ticket.Application.Service.CustomerMyTicketService;
import group.edu.bus_ticket.Application.Service.CustomerPaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Trang E3 - Thanh toan (Thymeleaf, khong JS). Gia lap cong thanh toan.
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
                      @RequestParam(required = false) PaymentMethod method,
                      Model model, RedirectAttributes ra) {
        try {
            PaymentResponse res = paymentService.pay(bookingId, currentCustomer.getAccountId(), method);
            ra.addFlashAttribute("flash", "Thanh toán thành công! Mã giao dịch: " + res.transactionCode()
                    + " • Số tiền: " + (res.amount() != null ? res.amount().longValue() : 0) + "đ");
            return "redirect:/customer/tickets/" + bookingId;
        } catch (ResponseStatusException ex) {
            model.addAttribute("error", ex.getReason());
            model.addAttribute("booking", myTicketService.getMyBooking(bookingId, currentCustomer.getAccountId()));
            return "customer/payment";
        }
    }
}
