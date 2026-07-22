package group.edu.bus_ticket.Domain.Entity;

import group.edu.bus_ticket.Domain.Enum.SeatStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "seat_availability",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trip_seat_segment",
                        columnNames = {
                                "trip_id",
                                "seat_code",
                                "start_station_order",
                                "end_station_order"
                        }
                )
        }
)
public class SeatAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "seat code can not blank")
    @Pattern(
            regexp = "^[A-Z]\\d{2}$",
            message = "seat code not in right format"
    )
    @Column(
            name = "seat_code",
            nullable = false,
            length = 10
    )
    private String seatCode;

    /*
     * Mỗi record đại diện cho trạng thái của một ghế
     * trên một đoạn tuyến cụ thể.
     */
    @NotNull(message = "start station order can not null")
    @Min(
            value = 0,
            message = "start station order can not negative"
    )
    @Column(
            name = "start_station_order",
            nullable = false
    )
    private Integer startStationOrder;

    @NotNull(message = "end station order can not null")
    @Min(
            value = 0,
            message = "end station order can not negative"
    )
    @Column(
            name = "end_station_order",
            nullable = false
    )
    private Integer endStationOrder;

    /*
     * AVAILABLE: ghế đang trống.
     * HELD: ghế đang được giữ tạm trong lúc tạo Booking.
     * BOOKED: ghế đã thanh toán và xác nhận.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status = SeatStatus.AVAILABLE;

    /*
     * Chỉ có giá trị khi status = HELD.
     * Khi quá thời điểm này, Service sẽ giải phóng ghế.
     */
    private LocalDateTime holdExpiredAt;

    /*
     * Optimistic locking.
     * Hibernate tăng version sau mỗi lần cập nhật record.
     * Nếu hai request cập nhật cùng một phiên bản,
     * một request sẽ bị từ chối.
     */
    @Version
    private Long version;

    /*
     * BookingDetail đang giữ hoặc đã đặt ghế này.
     * Null khi ghế AVAILABLE.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_detail_id")
    private BookingDetail bookingDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false
    )
    private Trip trip;

    public SeatAvailability() {
    }

    public SeatAvailability(
            String seatCode,
            Integer startStationOrder,
            Integer endStationOrder,
            SeatStatus status,
            LocalDateTime holdExpiredAt,
            BookingDetail bookingDetail,
            Trip trip
    ) {
        this.seatCode = seatCode;
        this.startStationOrder = startStationOrder;
        this.endStationOrder = endStationOrder;
        this.status = status;
        this.holdExpiredAt = holdExpiredAt;
        this.bookingDetail = bookingDetail;
        this.trip = trip;
    }

    /*
     * Điểm kết thúc phải nằm sau điểm bắt đầu.
     */
    @AssertTrue(
            message = "end station order must be after start station order"
    )
    public boolean isStationOrderValid() {
        if (
                startStationOrder == null ||
                        endStationOrder == null
        ) {
            return true;
        }

        return startStationOrder < endStationOrder;
    }

    /*
     * Kiểm tra ghế HELD đã hết hạn hay chưa.
     */
    public boolean isHoldExpired(LocalDateTime currentTime) {
        if (
                status != SeatStatus.HELD ||
                        holdExpiredAt == null ||
                        currentTime == null
        ) {
            return false;
        }

        return !holdExpiredAt.isAfter(currentTime);
    }

    /*
     * Giữ ghế tạm thời cho một BookingDetail.
     */
    public void hold(
            BookingDetail bookingDetail,
            LocalDateTime holdExpiredAt
    ) {
        this.status = SeatStatus.HELD;
        this.bookingDetail = bookingDetail;
        this.holdExpiredAt = holdExpiredAt;
    }

    /*
     * Xác nhận ghế sau khi Staff nhận thanh toán.
     */
    public void markAsBooked() {
        this.status = SeatStatus.BOOKED;
        this.holdExpiredAt = null;
    }

    /*
     * Giải phóng ghế khi Booking bị hủy hoặc hết hạn.
     */
    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.holdExpiredAt = null;
        this.bookingDetail = null;
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

    public void setStartStationOrder(
            Integer startStationOrder
    ) {
        this.startStationOrder = startStationOrder;
    }

    public Integer getEndStationOrder() {
        return endStationOrder;
    }

    public void setEndStationOrder(
            Integer endStationOrder
    ) {
        this.endStationOrder = endStationOrder;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public LocalDateTime getHoldExpiredAt() {
        return holdExpiredAt;
    }

    public void setHoldExpiredAt(
            LocalDateTime holdExpiredAt
    ) {
        this.holdExpiredAt = holdExpiredAt;
    }

    public Long getVersion() {
        return version;
    }

    public BookingDetail getBookingDetail() {
        return bookingDetail;
    }

    public void setBookingDetail(
            BookingDetail bookingDetail
    ) {
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SeatAvailability that =
                (SeatAvailability) o;

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
                ", status=" + status +
                ", holdExpiredAt=" + holdExpiredAt +
                ", version=" + version +
                '}';
    }
}