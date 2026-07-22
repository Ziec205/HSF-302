package group.edu.bus_ticket.Infrastructure.Persistence.Account;

import group.edu.bus_ticket.Domain.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountJpaRepo extends JpaRepository<Account, UUID> {

}
