package group.edu.bus_ticket.Infrastructure.Persistence.RouteStation;

import group.edu.bus_ticket.Domain.Entity.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RouteStationJpaRepo extends JpaRepository<RouteStation, UUID> {
}
