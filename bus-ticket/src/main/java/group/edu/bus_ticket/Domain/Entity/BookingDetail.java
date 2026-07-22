package group.edu.bus_ticket.Domain.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "booking_detail")
public class BookingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "passenger name can not blank")
    private String passengerName;

    @Min(value = 0, message = "ticket price can not negative")
    private Double ticketPrice;

    @Min(value = 0, message = "luggage weight can not blank")
    private Double luggageWeightKg;

    private Double luggageFee;

    private Double subTotal;

    @Column(nullable = false)
    private Boolean isReturnTicket = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public BookingDetail() {
    }

    public BookingDetail(String passengerName, Double ticketPrice, Double luggageWeightKg, Double luggageFee, Double subTotal, Boolean isReturnTicket, Booking booking) {
        this.passengerName = passengerName;
        this.ticketPrice = ticketPrice;
        this.luggageWeightKg = luggageWeightKg;
        this.luggageFee = luggageFee;
        this.subTotal = subTotal;
        this.isReturnTicket = isReturnTicket;
        this.booking = booking;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Double getLuggageWeightKg() {
        return luggageWeightKg;
    }

    public void setLuggageWeightKg(Double luggageWeightKg) {
        this.luggageWeightKg = luggageWeightKg;
    }

    public Double getLuggageFee() {
        return luggageFee;
    }

    public void setLuggageFee(Double luggageFee) {
        this.luggageFee = luggageFee;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Boolean getReturnTicket() {
        return isReturnTicket;
    }

    public void setReturnTicket(Boolean returnTicket) {
        isReturnTicket = returnTicket;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookingDetail that = (BookingDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BookingDetail{" +
                "id=" + id +
                ", passengerName='" + passengerName + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", luggageWeightKg=" + luggageWeightKg +
                ", luggageFee=" + luggageFee +
                ", subTotal=" + subTotal +
                ", isReturnTicket=" + isReturnTicket +
                ", booking=" + booking +
                '}';
    }
}
