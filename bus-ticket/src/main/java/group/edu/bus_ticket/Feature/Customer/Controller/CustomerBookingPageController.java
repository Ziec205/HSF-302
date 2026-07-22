package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Domain.Entity.Trip;
import group.edu.bus_ticket.Domain.Enum.BookingType;
import group.edu.bus_ticket.Feature.Customer.CurrentCustomerService;
import group.edu.bus_ticket.Feature.Customer.Dto.CreateBookingRequest;
import group.edu.bus_ticket.Feature.Customer.Dto.PassengerRequest;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerBookingService;
import group.edu.bus_ticket.Feature.Customer.Service.PricingService;
import group.edu.bus_ticket.Infrastructure.Persistence.Trip.TripJpaRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Trang E2 - Chon ghe & dat ve (Thymeleaf, khong JS).
 */
@Controller
public class CustomerBookingPageController {

    private final TripJpaRepo tripRepo;
    private final CustomerBookingService bookingService;
    private final PricingService pricingService;
    private final CurrentCustomerService currentCustomer;

    public CustomerBookingPageController(TripJpaRepo tripRepo, CustomerBookingService bookingService,
                                         PricingService pricingService, CurrentCustomerService currentCustomer) {
        this.tripRepo = tripRepo;
        this.bookingService = bookingService;
        this.pricingService = pricingService;
        this.currentCustomer = currentCustomer;
    }

    @GetMapping("/customer/book")
    public String bookPage(@RequestParam UUID tripId, Model model) {
        populate(model, tripId);
        return "customer/book";
    }

    @PostMapping("/customer/book")
    public String createBooking(@RequestParam UUID tripId,
                                @RequestParam(defaultValue = "ONEWAY") BookingType bookingType,
                                @RequestParam(required = false) String note,
                                @RequestParam(name = "selectedSeats", required = false) List<String> selectedSeats,
                                @RequestParam Map<String, String> allParams,
                                Model model) {
        try {
            if (selectedSeats == null || selectedSeats.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn ít nhất 1 ghế");
            }
            List<PassengerRequest> passengers = new ArrayList<>();
            for (String code : selectedSeats) {
                String name = allParams.get("name_" + code);
                if (name == null || name.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Vui lòng nhập tên hành khách cho ghế " + code);
                }
                Double luggage = parseDouble(allParams.get("luggage_" + code));
                passengers.add(new PassengerRequest(code, name.trim(), luggage));
            }

            CreateBookingRequest req = new CreateBookingRequest(
                    tripId, currentCustomer.getAccountId(), bookingType, note, passengers);
            var response = bookingService.createBooking(req);
            return "redirect:/customer/payment/" + response.bookingId();

        } catch (ResponseStatusException ex) {
            model.addAttribute("error", ex.getReason());
            populate(model, tripId);
            return "customer/book";
        }
    }

    private void populate(Model model, UUID tripId) {
        Trip trip = tripRepo.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chuyến"));
        model.addAttribute("trip", trip);
        model.addAttribute("seats", bookingService.getSeatMap(tripId));
        model.addAttribute("basePrice", pricingService.calcTicketPrice(trip, null, null));
    }

    private Double parseDouble(String v) {
        if (v == null || v.isBlank()) return 0.0;
        try {
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
