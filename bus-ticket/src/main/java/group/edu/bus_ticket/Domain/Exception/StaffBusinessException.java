package group.edu.bus_ticket.Domain.Exception;

public class StaffBusinessException extends RuntimeException {

    public StaffBusinessException(String message) {
        super(message);
    }

    public StaffBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}