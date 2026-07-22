package group.edu.bus_ticket.Domain.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "route_station")
public class RouteStation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Min(value = 0, message = "station order can not negative")
    private Integer stationOrder;

    @Min(value = 0, message = "price can not negative")
    private Double priceFromStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    public RouteStation() {
    }

    public RouteStation(Integer stationOrder, Double priceFromStart, Route route, Station station) {
        this.stationOrder = stationOrder;
        this.priceFromStart = priceFromStart;
        this.route = route;
        this.station = station;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getStationOrder() {
        return stationOrder;
    }

    public void setStationOrder(Integer stationOrder) {
        this.stationOrder = stationOrder;
    }

    public Double getPriceFromStart() {
        return priceFromStart;
    }

    public void setPriceFromStart(Double priceFromStart) {
        this.priceFromStart = priceFromStart;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RouteStation that = (RouteStation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RouteStation{" +
                "priceFromStart=" + priceFromStart +
                ", stationOrder=" + stationOrder +
                ", id=" + id +
                '}';
    }
}
