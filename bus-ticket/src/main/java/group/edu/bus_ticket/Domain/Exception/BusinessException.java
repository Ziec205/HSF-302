package group.edu.bus_ticket.Domain.Exception;

/**
 * Loi nghiep vu (vi pham quy tac dat/huy/thanh toan/danh gia...).
 * Chi mang thong diep cho nguoi dung - tang Presentation hien thi len trang,
 * khong gan HTTP status vi day la app Thymeleaf (server-side render), khong phai REST API.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
