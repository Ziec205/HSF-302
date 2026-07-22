package group.edu.bus_ticket.Feature.Customer;

import group.edu.bus_ticket.Domain.Entity.*;
import group.edu.bus_ticket.Domain.Enum.*;
import group.edu.bus_ticket.Infrastructure.Persistence.Account.AccountJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingDetailJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Bus.BusJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Route.RouteJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.RouteStation.RouteStationJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.SeatAvailability.SeatAvailabilityJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Station.StationJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Trip.TripJpaRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seed du lieu mau cho profile 'dev' de kiem thu toan bo luong Customer.
 * Chi chay khi CHUA co du lieu (tranh tao trung khi restart).
 */
@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final AccountJpaRepo accountRepo;
    private final StationJpaRepo stationRepo;
    private final RouteJpaRepo routeRepo;
    private final RouteStationJpaRepo routeStationRepo;
    private final BusJpaRepo busRepo;
    private final TripJpaRepo tripRepo;
    private final SeatAvailabilityJpaRepo seatRepo;
    private final BookingJpaRepo bookingRepo;
    private final BookingDetailJpaRepo bookingDetailRepo;

    public DevDataSeeder(AccountJpaRepo accountRepo, StationJpaRepo stationRepo, RouteJpaRepo routeRepo,
                         RouteStationJpaRepo routeStationRepo, BusJpaRepo busRepo, TripJpaRepo tripRepo,
                         SeatAvailabilityJpaRepo seatRepo, BookingJpaRepo bookingRepo,
                         BookingDetailJpaRepo bookingDetailRepo) {
        this.accountRepo = accountRepo;
        this.stationRepo = stationRepo;
        this.routeRepo = routeRepo;
        this.routeStationRepo = routeStationRepo;
        this.busRepo = busRepo;
        this.tripRepo = tripRepo;
        this.seatRepo = seatRepo;
        this.bookingRepo = bookingRepo;
        this.bookingDetailRepo = bookingDetailRepo;
    }

    @Override
    public void run(String... args) {
        if (tripRepo.count() > 0) {
            return;
        }
        log.info("[DevDataSeeder] Seeding du lieu mau cho profile dev...");

        Account customer = accountRepo.save(new Account(
                Status.AVAILABLE, "0900000001", "demo", CurrentCustomerService.DEMO_EMAIL,
                Role.CUSTOMER, "Nguyen Van Khach"));

        // Tuyen + diem dung (dung de tinh gia theo chang)
        Route route = routeRepo.save(new Route("Ha Noi - Sa Pa"));
        Station s0 = stationRepo.save(new Station("Ben xe My Dinh", Status.AVAILABLE, "Ha Noi"));
        Station s1 = stationRepo.save(new Station("Ben xe Sa Pa", Status.AVAILABLE, "Lao Cai"));
        routeStationRepo.save(new RouteStation(0, 0.0, route, s0));
        routeStationRepo.save(new RouteStation(1, 350000.0, route, s1));

        Bus bus = busRepo.save(new Bus("SwiftRide SR-402", "29B-123.45",
                BusType.SEATED, BusCapacity.SEAT_16, Status.AVAILABLE));

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(9).withMinute(30).withSecond(0).withNano(0);
        Trip tripA = tripRepo.save(new Trip("Hà Nội", "Sa Pa", tomorrow, "Trần Văn Tài",
                Status.AVAILABLE, route, bus));
        seedSeats(tripA);

        Trip tripB = tripRepo.save(new Trip("Hà Nội", "Sa Pa", tomorrow.withHour(14),
                "Lê Văn Xe", Status.AVAILABLE, route, bus));
        seedSeats(tripB);

        // Mot chuyen da di xong + don da thanh toan cua khach -> test E4/E6
        LocalDateTime yesterday = LocalDateTime.now().minusDays(2).withHour(8).withMinute(0).withSecond(0).withNano(0);
        Trip tripPast = tripRepo.save(new Trip("Đà Nẵng", "Hội An", yesterday, "Phạm Văn Lái",
                Status.AVAILABLE, route, bus));
        List<SeatAvailability> pastSeats = seedSeats(tripPast);

        Booking booking = new Booking(yesterday.minusDays(3), 350000.0, "Ve mau da hoan thanh",
                BookingType.ONEWAY, customer);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepo.save(booking);

        BookingDetail detail = bookingDetailRepo.save(new BookingDetail(
                "Nguyen Van Khach", 350000.0, 0.0, 0.0, 350000.0, false, booking));

        SeatAvailability seatA01 = pastSeats.get(0);
        seatA01.setStatus(SeatStatus.BOOKED);
        seatA01.setBookingDetail(detail);
        seatRepo.save(seatA01);

        log.info("[DevDataSeeder] Xong. Tai khoan khach: {} | 2 chuyen sap toi + 1 chuyen da di.",
                CurrentCustomerService.DEMO_EMAIL);
    }

    /** Sinh 16 ghe (A01-A08, B01-B08) cho mot chuyen. */
    private List<SeatAvailability> seedSeats(Trip trip) {
        List<SeatAvailability> seats = new ArrayList<>();
        for (char col : new char[]{'A', 'B'}) {
            for (int i = 1; i <= 8; i++) {
                String code = String.format("%c%02d", col, i);
                SeatAvailability seat = new SeatAvailability(code, 0, 1, null, trip);
                seat.setStatus(SeatStatus.AVAILABLE);
                seats.add(seatRepo.save(seat));
            }
        }
        return seats;
    }
}
