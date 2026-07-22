package group.edu.bus_ticket.Infrastructure.Persistence.Trip;

import group.edu.bus_ticket.Domain.Entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripJpaRepo extends JpaRepository<Trip, UUID> {
}
