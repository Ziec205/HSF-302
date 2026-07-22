package group.edu.bus_ticket.Domain.Entity;

import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import group.edu.bus_ticket.Domain.Enum.BookingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "`booking`")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
     * Mã Booking dùng để Staff và Customer tra cứu.
     * Giá trị này sẽ được sinh trong Service.
     */
    @Column(
            nullable = false,
            unique = true,
            length = 30
    )
    private String bookingCode;

    @Column(nullable = false)
    private LocalDateTime dateBooked;

    @Min(
            value = 0,
            message = "total price can not negative"
    )
    @Column(nullable = false)
    private Double totalPrice;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingType bookingType;

    /*
     * Trạng thái vòng đời của Booking.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    /*
     * Thông tin liên hệ vẫn được lưu trực tiếp trong Booking
     * vì khách mua tại quầy có thể chưa có Account.
     */
    @NotBlank(message = "contact name can not blank")
    @Column(nullable = false)
    private String contactName;

    @NotBlank(message = "contact phone can not blank")
    @Column(nullable = false)
    private String contactPhone;

    @Email(message = "contact email not in the right format")
    private String contactEmail;

    /*
     * Thời điểm Booking chờ thanh toán hết hạn.
     * Khi hết hạn, ghế đang giữ sẽ được giải phóng.
     */
    private LocalDateTime expiresAt;

    /*
     * Customer sở hữu Booking.
     * Có thể null nếu đây là khách vãng lai mua tại quầy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /*
     * Account thực hiện thao tác tạo Booking.
     * Trong luồng bán vé tại quầy, đây là Staff.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "created_by_id",
            nullable = false
    )
    private Account createdBy;

    /*
     * Trip mà khách đang mua vé.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false
    )
    private Trip trip;

    public Booking() {
    }

    public Booking(
            String bookingCode,
            LocalDateTime dateBooked,
            Double totalPrice,
            String note,
            BookingType bookingType,
            BookingStatus status,
            String contactName,
            String contactPhone,
            String contactEmail,
            LocalDateTime expiresAt,
            Account account,
            Account createdBy,
            Trip trip
    ) {
        this.bookingCode = bookingCode;
        this.dateBooked = dateBooked;
        this.totalPrice = totalPrice;
        this.note = note;
        this.bookingType = bookingType;
        this.status = status;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.expiresAt = expiresAt;
        this.account = account;
        this.createdBy = createdBy;
        this.trip = trip;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public LocalDateTime getDateBooked() {
        return dateBooked;
    }

    public void setDateBooked(LocalDateTime dateBooked) {
        this.dateBooked = dateBooked;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

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
                ", bookingCode='" + bookingCode + '\'' +
                ", dateBooked=" + dateBooked +
                ", totalPrice=" + totalPrice +
                ", note='" + note + '\'' +
                ", bookingType=" + bookingType +
                ", status=" + status +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}