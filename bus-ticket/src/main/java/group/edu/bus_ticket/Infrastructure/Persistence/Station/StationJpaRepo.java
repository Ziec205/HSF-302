package group.edu.bus_ticket.Infrastructure.Persistence.Station;

import group.edu.bus_ticket.Domain.Entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StationJpaRepo extends JpaRepository<Station, UUID> {
}
