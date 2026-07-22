package group.edu.bus_ticket.Domain.Entity;

import group.edu.bus_ticket.Domain.Enum.BookingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "`booking`")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime dateBooked;

    @Min(value = 0,message = "total price can not negative")
    private Double totalPrice;

    private String note;

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public Booking() {
    }

    public Booking(LocalDateTime dateBooked, Double totalPrice, String note, BookingType bookingType, Account account) {
        this.dateBooked = dateBooked;
        this.totalPrice = totalPrice;
        this.note = note;
        this.bookingType = bookingType;
        this.account = account;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getDateBooked() {
        return dateBooked;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public String getNote() {
        return note;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public Account getAccount() {
        return account;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDateBooked(LocalDateTime dateBooked) {
        this.dateBooked = dateBooked;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", dateBooked=" + dateBooked +
                ", totalPrice=" + totalPrice +
                ", note='" + note + '\'' +
                ", bookingType=" + bookingType +
                '}';
    }
}
