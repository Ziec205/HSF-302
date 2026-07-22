package group.edu.bus_ticket.Infrastructure.Persistence.Review;

import group.edu.bus_ticket.Domain.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewJpaRepo extends JpaRepository<Review, UUID> {

    /** Kiem tra 1 booking da duoc danh gia hay chua (chan danh gia trung - E6). */
    boolean existsByBooking_Id(UUID bookingId);

    /** Danh sach danh gia cua mot chuyen. */
    List<Review> findByTrip_IdOrderByCreatedAtDesc(UUID tripId);
}
