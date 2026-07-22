package group.edu.bus_ticket.Domain.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "seat_availability")
public class SeatAvailability {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @Pattern(regexp = "^[A-Z]\\d{2}$", message = "seat code not in right format")
    private String seatCode;

    @Min(value = 0, message = "start station order can not negative")
    private Integer startStationOrder;

    @Min(value = 0, message = "end station order can not negative")
    private Integer endStationOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_detail_id")
    private BookingDetail bookingDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    public SeatAvailability() {
    }

    public SeatAvailability(String seatCode, Integer startStationOrder, Integer endStationOrder, BookingDetail bookingDetail, Trip trip) {
        this.seatCode = seatCode;
        this.startStationOrder = startStationOrder;
        this.endStationOrder = endStationOrder;
        this.bookingDetail = bookingDetail;
        this.trip = trip;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public Integer getStartStationOrder() {
        return startStationOrder;
    }

    public void setStartStationOrder(Integer startStationOrder) {
        this.startStationOrder = startStationOrder;
    }

    public Integer getEndStationOrder() {
        return endStationOrder;
    }

    public void setEndStationOrder(Integer endStationOrder) {
        this.endStationOrder = endStationOrder;
    }

    public BookingDetail getBookingDetail() {
        return bookingDetail;
    }

    public void setBookingDetail(BookingDetail bookingDetail) {
        this.bookingDetail = bookingDetail;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SeatAvailability that = (SeatAvailability) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SeatAvailability{" +
                "id=" + id +
                ", seatCode='" + seatCode + '\'' +
                ", startStationOrder=" + startStationOrder +
                ", endStationOrder=" + endStationOrder +
                '}';
    }
}
