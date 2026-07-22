package group.edu.bus_ticket.Infrastructure.Persistence.Booking;

import group.edu.bus_ticket.Domain.Entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingDetailJpaRepo extends JpaRepository<BookingDetail, UUID> {
}
