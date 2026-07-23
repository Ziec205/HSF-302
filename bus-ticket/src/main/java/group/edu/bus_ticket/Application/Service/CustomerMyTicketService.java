package group.edu.bus_ticket.Application.Service;

import group.edu.bus_ticket.Domain.Entity.Booking;
import group.edu.bus_ticket.Domain.Entity.BookingDetail;
import group.edu.bus_ticket.Application.Dto.BookingResponse;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingDetailJpaRepo;
import group.edu.bus_ticket.Infrastructure.Persistence.Booking.BookingJpaRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import group.edu.bus_ticket.Domain.Exception.BusinessException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Chuc nang E4 - Quan ly ve cua toi.
 *
 * Happy case: khach xem lich su / ve sap di, xem chi tiet ma ve, gio, ghe.
 * Ngoai le: khong co ve -> tra ve danh sach rong.
 */
@Service
public class CustomerMyTicketService {

    private final BookingJpaRepo bookingRepo;
    private final BookingDetailJpaRepo bookingDetailRepo;
    private final BookingAssembler assembler;

    public CustomerMyTicketService(BookingJpaRepo bookingRepo,
                                   BookingDetailJpaRepo bookingDetailRepo,
                                   BookingAssembler assembler) {
        this.bookingRepo = bookingRepo;
        this.bookingDetailRepo = bookingDetailRepo;
        this.assembler = assembler;
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listMyBookings(UUID accountId) {
        return bookingRepo.findByAccount_IdOrderByDateBookedDesc(accountId).stream()
                .map(b -> assembler.toResponse(b, bookingDetailRepo.findByBooking_Id(b.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponse getMyBooking(UUID bookingId, UUID accountId) {
        Booking booking = loadOwnedBooking(bookingId, accountId);
        List<BookingDetail> details = bookingDetailRepo.findByBooking_Id(bookingId);
        return assembler.toResponse(booking, details);
    }

    /** Tai booking va kiem tra quyen so huu - dung chung cho E4/E5/E6. */
    Booking loadOwnedBooking(UUID bookingId, UUID accountId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Khong tim thay don dat ve"));
        if (booking.getAccount() == null || !booking.getAccount().getId().equals(accountId)) {
            throw new BusinessException("Ban khong co quyen tren don dat ve nay");
        }
        return booking;
    }
}
