package group.edu.bus_ticket.Infrastructure.Persistence.Booking;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Enum.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingJpaRepo extends JpaRepository<Booking, UUID> {

    /** Lich su dat ve cua mot khach hang, moi nhat truoc (E4). */
    List<Booking> findByAccount_IdOrderByDateBookedDesc(UUID accountId);

    /** Cac don PENDING_PAYMENT da qua han giu ghe - phuc vu job giai phong ghe. */
    List<Booking> findByStatusAndHoldExpiresAtBefore(BookingStatus status, LocalDateTime time);
}
