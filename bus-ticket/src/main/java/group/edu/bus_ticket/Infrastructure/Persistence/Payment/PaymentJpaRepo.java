package group.edu.bus_ticket.Infrastructure.Persistence.Payment;

import group.edu.bus_ticket.Domain.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepo extends JpaRepository<Payment, UUID> {

    /** Payment cua mot don dat ve (dung cho idempotency thanh toan). */
    Optional<Payment> findByBooking_Id(UUID bookingId);
}
