package group.edu.bus_ticket.Infrastructure.Persistence.Route;

import group.edu.bus_ticket.Domain.Entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RouteJpaRepo extends JpaRepository<Route, UUID> {
}
