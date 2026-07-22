package group.edu.bus_ticket.Infrastructure.Persistence.Bus;

import group.edu.bus_ticket.Domain.Entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BusJpaRepo extends JpaRepository<Bus, UUID> {
}
