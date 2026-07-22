package group.edu.bus_ticket.Infrastructure.Persistence.Transaction;

import group.edu.bus_ticket.Domain.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionJpaRepo extends JpaRepository<Transaction, UUID> {
}
