package group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability;

import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatAvailabilityJpaRepo extends JpaRepository<SeatAvailability, UUID> {
}
