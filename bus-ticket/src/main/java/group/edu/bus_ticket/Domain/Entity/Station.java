package group.edu.bus_ticket.Domain.Entity;

import group.edu.bus_ticket.Domain.Enum.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "station")
public class Station {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "name can not blank")
    private String name;

    @NotBlank(message = "address can not blank")
    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Station() {
    }

    public Station(String name, Status status, String address) {
        this.name = name;
        this.status = status;
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                '}';
    }
}
