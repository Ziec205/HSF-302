package group.edu.bus_ticket.Feature.Customer.Controller;

import group.edu.bus_ticket.Feature.Customer.Dto.ReviewRequest;
import group.edu.bus_ticket.Feature.Customer.Dto.ReviewResponse;
import group.edu.bus_ticket.Feature.Customer.Service.CustomerReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * API danh gia chuyen di cho khach hang (E6).
 * POST /api/customer/reviews
 */
@RestController
@RequestMapping("/api/customer/reviews")
public class CustomerReviewController {

    private final CustomerReviewService reviewService;

    public CustomerReviewController(CustomerReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@Valid @RequestBody ReviewRequest request) {
        return reviewService.createReview(request);
    }
}
