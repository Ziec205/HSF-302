package group.edu.bus_ticket.Infrastructure.Persistence.Payment;

import group.edu.bus_ticket.Domain.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentJpaRepo extends JpaRepository<Payment, UUID> {
}
