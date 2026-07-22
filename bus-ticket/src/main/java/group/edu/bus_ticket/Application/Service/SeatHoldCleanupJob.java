package group.edu.bus_ticket.Application.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job dinh ky giai phong ghe cua cac don giu ghe qua han (nghiep vu F - timeout giu ghe).
 * Chay moi 60 giay.
 */
@Component
public class SeatHoldCleanupJob {

    private final SeatReleaseService seatReleaseService;

    public SeatHoldCleanupJob(SeatReleaseService seatReleaseService) {
        this.seatReleaseService = seatReleaseService;
    }

    @Scheduled(fixedDelayString = "${app.booking.cleanup-interval-ms:60000}")
    public void run() {
        seatReleaseService.releaseExpiredHolds();
    }
}
