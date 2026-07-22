package group.edu.bus_ticket.Infrastructure.Persistence.RouteStation;

import group.edu.bus_ticket.Domain.Entity.RouteStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteStationJpaRepo extends JpaRepository<RouteStation, UUID> {

    /** Cac diem dung cua mot tuyen, sap xep theo thu tu - dung de tinh gia ve theo chang. */
    List<RouteStation> findByRoute_IdOrderByStationOrder(UUID routeId);
}
