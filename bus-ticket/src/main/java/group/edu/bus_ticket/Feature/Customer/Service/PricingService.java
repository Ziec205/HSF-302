package group.edu.bus_ticket.Feature.Customer.Service;

import group.edu.bus_ticket.Domain.Entity.RouteStation;
import group.edu.bus_ticket.Domain.Entity.Trip;
import group.edu.bus_ticket.Infrastructure.Persistence.RouteStation.RouteStationJpaRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tinh gia ve theo chang va phi hanh ly cho luong dat ve Customer.
 *
 * Gia ve = priceFromStart(diem xuong) - priceFromStart(diem len) tren tuyen cua chuyen.
 * Neu ghe khong khai bao thu tu diem len/xuong thi mac dinh tinh tron tuyen.
 */
@Service
public class PricingService {

    // Mien phi 20kg dau, sau do tinh phi theo kg.
    private static final double FREE_LUGGAGE_KG = 20.0;
    private static final double LUGGAGE_FEE_PER_KG = 10_000.0;

    private final RouteStationJpaRepo routeStationRepo;

    public PricingService(RouteStationJpaRepo routeStationRepo) {
        this.routeStationRepo = routeStationRepo;
    }

    /** Gia ve co ban cho mot chang tren chuyen. */
    public double calcTicketPrice(Trip trip, Integer startOrder, Integer endOrder) {
        if (trip.getRoute() == null) {
            return 0.0;
        }
        List<RouteStation> stations = routeStationRepo.findByRoute_IdOrderByStationOrder(trip.getRoute().getId());
        if (stations.isEmpty()) {
            return 0.0;
        }
        Map<Integer, Double> priceByOrder = stations.stream()
                .collect(Collectors.toMap(RouteStation::getStationOrder,
                        rs -> rs.getPriceFromStart() != null ? rs.getPriceFromStart() : 0.0,
                        (a, b) -> a));

        int firstOrder = stations.get(0).getStationOrder();
        int lastOrder = stations.get(stations.size() - 1).getStationOrder();

        int from = startOrder != null ? startOrder : firstOrder;
        int to = endOrder != null ? endOrder : lastOrder;

        double priceFrom = priceByOrder.getOrDefault(from, 0.0);
        double priceTo = priceByOrder.getOrDefault(to, priceByOrder.get(lastOrder));

        double price = priceTo - priceFrom;
        return Math.max(price, 0.0);
    }

    /** Phi hanh ly theo khoi luong (kg). */
    public double calcLuggageFee(Double luggageWeightKg) {
        if (luggageWeightKg == null || luggageWeightKg <= FREE_LUGGAGE_KG) {
            return 0.0;
        }
        return (luggageWeightKg - FREE_LUGGAGE_KG) * LUGGAGE_FEE_PER_KG;
    }
}
