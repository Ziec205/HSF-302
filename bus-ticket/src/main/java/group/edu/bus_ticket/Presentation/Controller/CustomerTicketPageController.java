package group.edu.bus_ticket.Presentation.Controller;

import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Application.Service.CurrentCustomerService;
import group.edu.bus_ticket.Application.Dto.BookingResponse;
import group.edu.bus_ticket.Application.Dto.CancelBookingResponse;
import group.edu.bus_ticket.Application.Dto.ReviewRequest;
import group.edu.bus_ticket.Application.Service.CustomerCancelService;
import group.edu.bus_ticket.Application.Service.CustomerMyTicketService;
import group.edu.bus_ticket.Application.Service.CustomerReviewService;
import group.edu.bus_ticket.Infrastructure.Persistence.Payment.PaymentJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Review.ReviewJpaRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Trang E4 (ve cua toi + chi tiet), E5 (huy ve), E6 (danh gia) - Thymeleaf, khong JS.
 */
@Controller
@RequestMapping("/customer/tickets")
public class CustomerTicketPageController {

    private final CustomerMyTicketService myTicketService;
    private final CustomerCancelService cancelService;
    private final CustomerReviewService reviewService;
    private final ReviewJpaRepo reviewRepo;
    private final PaymentJpaRepo paymentRepo;
    private final CurrentCustomerService currentCustomer;

    public CustomerTicketPageController(CustomerMyTicketService myTicketService,
                                        CustomerCancelService cancelService,
                                        CustomerReviewService reviewService,
                                        ReviewJpaRepo reviewRepo,
                                        PaymentJpaRepo paymentRepo,
                                        CurrentCustomerService currentCustomer) {
        this.myTicketService = myTicketService;
        this.cancelService = cancelService;
        this.reviewService = reviewService;
        this.reviewRepo = reviewRepo;
        this.paymentRepo = paymentRepo;
        this.currentCustomer = currentCustomer;
    }

    // ---------- E4: danh sach ve cua toi ----------
    @GetMapping
    public String myTickets(@RequestParam(required = false) String status, Model model) {
        UUID accountId = currentCustomer.getAccountId();
        List<BookingResponse> all = myTicketService.listMyBookings(accountId);
        LocalDateTime now = LocalDateTime.now();

        String tab = status == null ? "all" : status;
        List<BookingResponse> filtered = all.stream()
                .filter(b -> matchTab(b, tab, now))
                .collect(Collectors.toList());

        Set<UUID> reviewedIds = all.stream()
                .filter(b -> reviewRepo.existsByBooking_Id(b.bookingId()))
                .map(BookingResponse::bookingId)
                .collect(Collectors.toSet());

        model.addAttribute("bookings", filtered);
        model.addAttribute("tab", tab);
        model.addAttribute("reviewedIds", reviewedIds);
        return "customer/my-tickets";
    }

    private boolean matchTab(BookingResponse b, String tab, LocalDateTime now) {
        BookingStatus s = b.status();
        boolean departed = b.departureTime() != null && !b.departureTime().isAfter(now);
        return switch (tab) {
            case "pending" -> s == BookingStatus.PENDING_PAYMENT;
            case "confirmed" -> s == BookingStatus.CONFIRMED && !departed;
            case "done" -> s == BookingStatus.CONFIRMED && departed;
            case "cancelled" -> s == BookingStatus.CANCELLED || s == BookingStatus.EXPIRED
                    || s == BookingStatus.REFUND_PENDING || s == BookingStatus.REFUNDED;
            default -> true;
        };
    }

    // ---------- E4: chi tiet ----------
    @GetMapping("/{bookingId}")
    public String detail(@PathVariable UUID bookingId, Model model) {
        BookingResponse booking = myTicketService.getMyBooking(bookingId, currentCustomer.getAccountId());
        model.addAttribute("booking", booking);
        model.addAttribute("canReview", !reviewRepo.existsByBooking_Id(bookingId));
        model.addAttribute("payment", paymentRepo.findByBooking_Id(bookingId).orElse(null));
        return "customer/booking-detail";
    }

    // ---------- E5: huy ve ----------
    @PostMapping("/{bookingId}/cancel")
    public String cancel(@PathVariable UUID bookingId, RedirectAttributes ra) {
        try {
            CancelBookingResponse res = cancelService.cancel(bookingId, currentCustomer.getAccountId());
            ra.addFlashAttribute("flash", res.message()
                    + (res.refundAmount() != null && res.refundAmount() > 0
                    ? " (Hoàn: " + res.refundAmount().longValue() + "đ)" : ""));
        } catch (ResponseStatusException ex) {
            ra.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/customer/tickets/" + bookingId;
    }

    // ---------- E6: form danh gia ----------
    @GetMapping("/{bookingId}/review")
    public String reviewForm(@PathVariable UUID bookingId, Model model) {
        model.addAttribute("booking", myTicketService.getMyBooking(bookingId, currentCustomer.getAccountId()));
        return "customer/review";
    }

    @PostMapping("/{bookingId}/review")
    public String submitReview(@PathVariable UUID bookingId,
                               @RequestParam Integer rating,
                               @RequestParam(required = false) String comment,
                               Model model, RedirectAttributes ra) {
        try {
            reviewService.createReview(new ReviewRequest(bookingId, currentCustomer.getAccountId(), rating, comment));
            ra.addFlashAttribute("flash", "Cảm ơn bạn đã đánh giá chuyến đi!");
            return "redirect:/customer/tickets/" + bookingId;
        } catch (ResponseStatusException ex) {
            model.addAttribute("error", ex.getReason());
            model.addAttribute("booking", myTicketService.getMyBooking(bookingId, currentCustomer.getAccountId()));
            return "customer/review";
        }
    }
}
