package group.edu.bus_ticket.Presentation.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Bat loi toan cuc cho tang Presentation.
 * Tra ve trang loi (Thymeleaf) thay vi JSON.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        log.warn("Yeu cau khong hop le: {}", ex.getMessage());
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
