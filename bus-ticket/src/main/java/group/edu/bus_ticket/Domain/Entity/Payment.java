package group.edu.bus_ticket.Domain.Entity;

import group.edu.bus_ticket.Domain.Enum.PaymentMethod;
import group.edu.bus_ticket.Domain.Enum.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime createPayment;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // So tien thanh toan
    private Double amount;

    // Trang thai thanh toan: PENDING -> PAID / FAILED / REFUNDED
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Ma giao dich gia lap (mo phong cong thanh toan)
    private String transactionCode;

    // Thoi diem thanh toan thanh cong
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public Payment() {
    }

    public Payment(LocalDateTime createPayment, PaymentMethod paymentMethod, Booking booking) {
        this.createPayment = createPayment;
        this.paymentMethod = paymentMethod;
        this.booking = booking;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatePayment() {
        return createPayment;
    }

    public void setCreatePayment(LocalDateTime createPayment) {
        this.createPayment = createPayment;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
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
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
