package group.edu.bus_ticket.Application.Service;

import group.edu.bus_ticket.Domain.Entity.Account;
import group.edu.bus_ticket.Domain.Enum.Role;
import group.edu.bus_ticket.Infrastructure.Persistence.Account.AccountJpaRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Cung cap "khach hang dang dang nhap" cho cac trang Customer.
 *
 * TAM THOI: tra ve tai khoan CUSTOMER dau tien trong he thong (du lieu mau dev).
 * Khi module Authentication (Quan) hoan thien, thay bang tai khoan lay tu
 * session/JWT cua nguoi dung.
 */
@Service
public class CurrentCustomerService {

    public static final String DEMO_EMAIL = "customer@demo.com";

    private final AccountJpaRepo accountRepo;

    public CurrentCustomerService(AccountJpaRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    public Account getAccount() {
        return accountRepo.findAll().stream()
                .filter(a -> a.getRole() == Role.CUSTOMER)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Chua co tai khoan khach hang. Hay chay profile 'dev' de seed du lieu mau."));
    }

    public UUID getAccountId() {
        return getAccount().getId();
    }
}
