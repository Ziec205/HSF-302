package group.edu.bus_ticket.Domain.Entity;

import group.edu.bus_ticket.Domain.Enum.BusType;
import group.edu.bus_ticket.Domain.Enum.BusCapacity;
import group.edu.bus_ticket.Domain.Enum.Status;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "bus")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String busName;

    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private BusType busType;

    @Enumerated(EnumType.STRING)
    private BusCapacity capacity;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Bus() {
    }

    public Bus(String busName, String licensePlate, BusType busType, BusCapacity capacity, Status status) {
        this.busName = busName;
        this.licensePlate = licensePlate;
        this.busType = busType;
        this.capacity = capacity;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public BusType getBusType() {
        return busType;
    }

    public void setBusType(BusType busType) {
        this.busType = busType;
    }

    public BusCapacity getCapacity() {
        return capacity;
    }

    public void setCapacity(BusCapacity capacity) {
        this.capacity = capacity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Bus bus = (Bus) o;
        return Objects.equals(id, bus.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Bus{" +
                "id=" + id +
                ", busName='" + busName + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", busType=" + busType +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }
}
