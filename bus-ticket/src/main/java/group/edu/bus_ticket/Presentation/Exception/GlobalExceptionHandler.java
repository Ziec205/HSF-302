package group.edu.bus_ticket.Presentation.Exception;

import group.edu.bus_ticket.Domain.Exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Bat loi toan cuc cho tang Presentation.
 * Tra ve trang loi (Thymeleaf), khong tra HTTP status vi day khong phai REST API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, Model model) {
        log.warn("Loi nghiep vu: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, Model model) {
        log.error("Loi khong mong doi", ex);
        model.addAttribute("message", "Da co loi xay ra, vui long thu lai.");
        return "error";
    }
}
