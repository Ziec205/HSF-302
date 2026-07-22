package group.edu.bus_ticket.Infrastructure.Persistence.Booking;

import group.edu.bus_ticket.Domain.Entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingDetailJpaRepo extends JpaRepository<BookingDetail, UUID> {

    /** Cac dong chi tiet (moi ghe/hanh khach) cua mot don dat ve. */
    List<BookingDetail> findByBooking_Id(UUID bookingId);
}
