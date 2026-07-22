package group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability;

import group.edu.bus_ticket.Domain.Entity.SeatAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SeatAvailabilityJpaRepo extends JpaRepository<SeatAvailability, UUID> {

    /** Toan bo ghe cua mot chuyen, dung de ve so do ghe (E2). */
    List<SeatAvailability> findByTrip_IdOrderBySeatCode(UUID tripId);

    /** Cac ghe thuoc mot don dat ve (qua booking_detail) - dung de dung chi tiet ve. */
    List<SeatAvailability> findByBookingDetail_Booking_Id(UUID bookingId);

    /**
     * Khoa bi quan (pessimistic write) cac ghe duoc chon de tranh 2 nguoi dat trung 1 ghe
     * (xu ly race condition trong chuc nang E2 / nghiep vu giu ghe tam).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatAvailability s WHERE s.trip.id = :tripId AND s.seatCode IN :codes")
    List<SeatAvailability> lockSeats(@Param("tripId") UUID tripId, @Param("codes") List<String> codes);

    /** Cac ghe dang giu tam (HELD) da qua han - phuc vu job giai phong ghe. */
    @Query("SELECT s FROM SeatAvailability s " +
            "WHERE s.status = group.edu.bus_ticket.Domain.Enum.SeatStatus.HELD " +
            "AND s.heldUntil < :now")
    List<SeatAvailability> findExpiredHolds(@Param("now") LocalDateTime now);
}
