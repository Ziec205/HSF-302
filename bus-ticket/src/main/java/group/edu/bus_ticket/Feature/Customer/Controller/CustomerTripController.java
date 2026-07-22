package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Feature.Customer.Dto.TripSearchResponse;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerTripService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * API tim kiem chuyen cho web khach hang (E1).
 * Vi du: GET /api/customer/trips?from=Ha Noi&to=Hai Phong&date=2026-08-01
 */
@RestController
@RequestMapping("/api/customer/trips")
public class CustomerTripController {

    private final CustomerTripService tripService;

    public CustomerTripController(CustomerTripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public List<TripSearchResponse> search(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return tripService.searchTrips(from, to, date);
    }
}
