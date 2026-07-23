package group.edu.bus_ticket.Presentation.Controller;

import group.edu.bus_ticket.Application.Service.CustomerTripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import group.edu.bus_ticket.Domain.Exception.BusinessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Trang khach hang (Thymeleaf, server-side render) - E1: Trang chu & Tim kiem chuyen.
 */
@Controller
public class CustomerHomePageController {

    private final CustomerTripService tripService;

    public CustomerHomePageController(CustomerTripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping({"/customer", "/customer/home"})
    public String home(Model model) {
        model.addAttribute("today", LocalDate.now().toString());
        model.addAttribute("popular", List.of(
                Map.of("label", "Hà Nội đi Sa Pa", "note", "Từ 350.000đ", "from", "Hà Nội", "to", "Sa Pa"),
                Map.of("label", "TP. HCM đi Đà Lạt", "note", "Từ 250.000đ", "from", "Hồ Chí Minh", "to", "Đà Lạt"),
                Map.of("label", "Đà Nẵng đi Huế", "note", "Từ 120.000đ", "from", "Đà Nẵng", "to", "Huế")
        ));
        return "customer/home";
    }

    @GetMapping("/customer/trips")
    public String search(@RequestParam(required = false) String from,
                         @RequestParam(required = false) String to,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                         Model model) {
        model.addAttribute("today", LocalDate.now().toString());
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("date", date != null ? date.toString() : LocalDate.now().toString());

        if (from != null && to != null && date != null) {
            try {
                model.addAttribute("trips", tripService.searchTrips(from, to, date));
            } catch (BusinessException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        return "customer/search";
    }
}
