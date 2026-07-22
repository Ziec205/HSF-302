# Customer — Việt

Chức năng dành cho **Khách hàng** (mục E trong FunctionList).

| Mã | Chức năng | Trạng thái |
|---|---|---|
| E1 | Tìm kiếm chuyến (điểm đi, điểm đến, ngày) | ✅ Hoàn thành |
| E2 | Chọn ghế & đặt vé (giữ ghế tạm có timeout) | ✅ Hoàn thành |
| E3 | Thanh toán | ⏳ Placeholder (Payment làm sau) |
| E4 | Quản lý vé của tôi | ✅ Hoàn thành |
| E5 | Hủy vé / yêu cầu hoàn tiền | ✅ Hoàn thành |
| E6 | Đánh giá & phản hồi | ✅ Hoàn thành |

## Cấu trúc

```
Customer/
├── Controller/                REST controller cho web khách hàng
│   ├── CustomerTripController      (E1)
│   ├── CustomerBookingController   (E2)
│   ├── CustomerPaymentController   (E3 placeholder)
│   ├── CustomerMyTicketController  (E4)
│   ├── CustomerCancelController    (E5)
│   └── CustomerReviewController    (E6)
├── Service/                   Nghiệp vụ Customer
│   ├── CustomerTripService         (E1: tìm chuyến, đếm ghế trống)
│   ├── CustomerBookingService      (E2: sơ đồ ghế, giữ ghế, tạo booking)
│   ├── PricingService              (giá vé theo chặng + phí hành lý)
│   ├── BookingAssembler            (dựng BookingResponse dùng chung)
│   ├── CustomerPaymentService      (E3 placeholder: xác nhận đơn)
│   ├── CustomerMyTicketService     (E4: vé của tôi + kiểm tra sở hữu)
│   ├── CustomerCancelService       (E5: hủy + chính sách hoàn tiền)
│   ├── SeatReleaseService          (F: giải phóng ghế khi hủy/hết hạn)
│   ├── SeatHoldCleanupJob          (F: job định kỳ 60s)
│   └── CustomerReviewService       (E6: đánh giá sau chuyến)
├── Dto/                       Request/Response DTO (record)
└── CustomerSchedulingConfig.java   Bật @EnableScheduling cho các job Customer
```

## Danh sách API (`/api/customer/**`)

| Method | Endpoint | Chức năng |
|---|---|---|
| GET  | `/api/customer/trips?from=&to=&date=` | E1 — tìm chuyến |
| GET  | `/api/customer/trips/{tripId}/seats` | E2 — sơ đồ ghế |
| POST | `/api/customer/bookings` | E2 — tạo đơn (giữ ghế tạm) |
| POST | `/api/customer/bookings/{id}/pay?accountId=` | E3 — thanh toán (placeholder) |
| GET  | `/api/customer/bookings?accountId=` | E4 — danh sách vé của tôi |
| GET  | `/api/customer/bookings/{id}?accountId=` | E4 — chi tiết đơn |
| POST | `/api/customer/bookings/{id}/cancel?accountId=` | E5 — hủy / hoàn tiền |
| POST | `/api/customer/reviews` | E6 — đánh giá chuyến |

## Ghi chú kỹ thuật

- **Giữ ghế tạm (E2/F):** khoá bi quan (pessimistic lock) khi đặt ghế để chống race
  condition; ghế `HELD` kèm `holdExpiresAt`, job dọn định kỳ giải phóng ghế quá hạn.
- **Trạng thái đơn:** `PENDING_PAYMENT → CONFIRMED / CANCELLED / EXPIRED / REFUND_PENDING`.
- **Hoàn tiền (E5):** ≥24h hoàn 100%, ≥2h hoàn 50%, <2h không hoàn.
- **`accountId`** hiện truyền qua request; khi module Authentication (Quân) xong sẽ lấy
  từ người dùng đăng nhập thay vì tin client.
- Cấu hình DB nằm ở `application.properties` (đã gitignore — mỗi người tự cấu hình).
