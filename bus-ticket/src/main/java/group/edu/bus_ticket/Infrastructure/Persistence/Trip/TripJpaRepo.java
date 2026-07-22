package group.edu.bus_ticket.Infrastructure.Persistence.Trip;

import group.edu.bus_ticket.Domain.Entity.Trip;
import group.edu.bus_ticket.Domain.Enum.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TripJpaRepo extends JpaRepository<Trip, UUID> {

    /**
     * Tim chuyen theo diem di / diem den (khong phan biet hoa thuong) va khoang thoi gian
     * khoi hanh trong ngay, chi lay chuyen dang hoat dong. Phuc vu chuc nang E1.
     */
    @Query("SELECT t FROM Trip t " +
            "WHERE LOWER(t.destinationFrom) = LOWER(:from) " +
            "AND LOWER(t.destinationTo) = LOWER(:to) " +
            "AND t.departureTime BETWEEN :start AND :end " +
            "AND t.status = :status " +
            "ORDER BY t.departureTime ASC")
    List<Trip> searchTrips(@Param("from") String from,
                           @Param("to") String to,
                           @Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end,
                           @Param("status") Status status);
}
