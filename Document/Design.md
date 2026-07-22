# THIẾT KẾ GIAO DIỆN — KHÁCH HÀNG (CUSTOMER)

> Tài liệu đặc tả UI cho **web khách hàng** của hệ thống đặt vé xe bus.
> Dùng làm đầu vào cho công cụ thiết kế (Stitch): mô tả **màn hình, thành phần,
> thuộc tính dữ liệu, giá trị hợp lệ, trạng thái và thông báo**.
>
> Phạm vi: các chức năng **E1–E6** của Customer. Nghiệp vụ chi tiết xem
> [FunctionList.md](FunctionList.md). Backend đã hiện thực dưới dạng REST API
> tiền tố `/api/customer/**` (cùng app, cùng origin với FE Thymeleaf).

---

## 0. TỔNG QUAN LUỒNG NGƯỜI DÙNG

```
[Trang chủ / Tìm chuyến]  E1
        │  chọn 1 chuyến
        ▼
[Sơ đồ ghế + nhập hành khách]  E2  ── giữ ghế tạm (đếm ngược 10 phút)
        │  đặt vé
        ▼
[Thanh toán]  E3  ── xác nhận → vé có hiệu lực
        │
        ▼
[Vé của tôi]  E4 ──► [Chi tiết vé] ──► [Hủy vé / Hoàn tiền]  E5
                                   └──► [Đánh giá chuyến]      E6
```

**Nguyên tắc hiển thị:** giao diện khách hàng tách riêng (khu vực `/customer/**`),
màu sắc/thương hiệu thân thiện. Mọi ràng buộc dữ liệu đều được BE kiểm tra lại
(FE chỉ hỗ trợ trải nghiệm, không phải nơi bảo vệ dữ liệu).

---

## 1. BẢNG GIÁ TRỊ (ENUM) — LABEL & MÀU GỢI Ý

### 1.1. Trạng thái đơn đặt vé — `BookingStatus`
| Giá trị (hệ thống) | Nhãn hiển thị | Ý nghĩa | Màu gợi ý |
|---|---|---|---|
| `PENDING_PAYMENT` | Chờ thanh toán | Đang giữ ghế tạm, cần thanh toán trước khi hết hạn | Cam / Vàng |
| `CONFIRMED` | Đã thanh toán | Vé có hiệu lực | Xanh lá |
| `CANCELLED` | Đã hủy | Đã hủy, không hoàn tiền | Xám |
| `EXPIRED` | Hết hạn giữ ghế | Quá thời gian giữ ghế, tự hủy | Xám |
| `REFUND_PENDING` | Chờ hoàn tiền | Đã hủy, đang chờ hoàn tiền | Xanh dương |
| `REFUNDED` | Đã hoàn tiền | Hoàn tiền xong | Xanh dương đậm |

### 1.2. Trạng thái ghế — `SeatStatus`
| Giá trị | Nhãn | Ý nghĩa trên sơ đồ ghế | Màu gợi ý |
|---|---|---|---|
| `AVAILABLE` | Ghế trống | Chọn được | Trắng/Xanh nhạt (viền) |
| `HELD` | Đang giữ | Người khác đang giữ tạm | Vàng (khóa) |
| `BOOKED` | Đã đặt | Không chọn được | Đỏ/Xám đậm |
| *(đang chọn)* | Ghế bạn chọn | Trạng thái local khi user tick | Xanh dương đậm |

### 1.3. Loại đặt vé — `BookingType`
| Giá trị | Nhãn |
|---|---|
| `ONEWAY` | Một chiều |
| `ROUNDTRIP` | Khứ hồi |

### 1.4. Phương thức thanh toán — `PaymentMethod` *(E3 — dùng khi module Payment hoàn thiện)*
| Giá trị | Nhãn |
|---|---|
| `COD` | Thanh toán khi lên xe |
| `CASH` | Tiền mặt |
| `BANK_TRANSFER` | Chuyển khoản ngân hàng |

### 1.5. Loại xe — `BusType` / Sức chứa — `BusCapacity`
| Enum | Giá trị | Nhãn |
|---|---|---|
| BusType | `SEATED` | Ghế ngồi |
| BusType | `SLEEPER` | Giường nằm |
| BusCapacity | `SEAT_16` | 16 chỗ |

---

## 2. TỪ ĐIỂN DỮ LIỆU (DATA DICTIONARY) — CUSTOMER

### 2.1. Chuyến đi (kết quả tìm kiếm) — `TripSearchResponse`
| Thuộc tính | Kiểu | Nhãn hiển thị | Ghi chú |
|---|---|---|---|
| `tripId` | UUID | (ẩn) | Khóa để chọn chuyến |
| `destinationFrom` | text | Điểm đi | |
| `destinationTo` | text | Điểm đến | |
| `departureTime` | datetime | Giờ khởi hành | Định dạng `dd/MM/yyyy HH:mm` |
| `busName` | text | Nhà xe / Tên xe | Có thể trống |
| `availableSeats` | số nguyên | Số ghế trống | 0 → hiển thị "Hết ghế" |

### 2.2. Ghế — `SeatDto`
| Thuộc tính | Kiểu | Nhãn | Ghi chú |
|---|---|---|---|
| `seatCode` | text | Mã ghế | Định dạng `^[A-Z]\d{2}$`, ví dụ `A01`, `B12` |
| `status` | SeatStatus | Trạng thái ghế | Xem 1.2 |
| `free` | boolean | Có chọn được không | `true` = cho phép chọn |

### 2.3. Hành khách nhập khi đặt — `PassengerRequest`
| Thuộc tính | Kiểu | Nhãn | Ràng buộc | Thông báo lỗi |
|---|---|---|---|---|
| `seatCode` | text | Ghế | Đúng định dạng `A01` | "Mã ghế không đúng định dạng (ví dụ A01)" |
| `passengerName` | text | Họ tên hành khách | Không được trống | "Tên hành khách không được để trống" |
| `luggageWeightKg` | số thực | Hành lý (kg) | ≥ 0, có thể bỏ trống | "Khối lượng hành lý không được âm" |

### 2.4. Yêu cầu đặt vé — `CreateBookingRequest`
| Thuộc tính | Kiểu | Nhãn | Ràng buộc |
|---|---|---|---|
| `tripId` | UUID | Chuyến | Bắt buộc |
| `accountId` | UUID | Tài khoản khách | Bắt buộc *(tạm truyền; sau lấy từ đăng nhập)* |
| `bookingType` | BookingType | Loại vé | Bắt buộc (Một chiều/Khứ hồi) |
| `note` | text | Ghi chú | Tùy chọn |
| `passengers` | danh sách | Danh sách hành khách/ghế | ≥ 1, **tối đa 5 ghế/lần** |

### 2.5. Vé (một dòng trong đơn) — `TicketDto`
| Thuộc tính | Kiểu | Nhãn |
|---|---|---|
| `bookingDetailId` | UUID | Mã vé |
| `seatCode` | text | Ghế |
| `passengerName` | text | Hành khách |
| `ticketPrice` | tiền | Giá vé |
| `luggageFee` | tiền | Phí hành lý |
| `subTotal` | tiền | Thành tiền (vé + hành lý) |

### 2.6. Đơn đặt vé (phản hồi) — `BookingResponse`
| Thuộc tính | Kiểu | Nhãn | Ghi chú |
|---|---|---|---|
| `bookingId` | UUID | Mã đơn | Hiển thị/tra cứu |
| `status` | BookingStatus | Trạng thái | Badge màu, xem 1.1 |
| `totalPrice` | tiền | Tổng tiền | |
| `dateBooked` | datetime | Ngày đặt | |
| `holdExpiresAt` | datetime | Hạn giữ ghế | Dùng cho **đồng hồ đếm ngược** khi `PENDING_PAYMENT` |
| `tripId` | UUID | (ẩn) | |
| `destinationFrom` | text | Điểm đi | |
| `destinationTo` | text | Điểm đến | |
| `departureTime` | datetime | Giờ khởi hành | |
| `tickets` | danh sách `TicketDto` | Danh sách vé | |

### 2.7. Kết quả hủy vé — `CancelBookingResponse`
| Thuộc tính | Kiểu | Nhãn |
|---|---|---|
| `bookingId` | UUID | Mã đơn |
| `status` | BookingStatus | Trạng thái mới |
| `refundAmount` | tiền | Số tiền được hoàn |
| `message` | text | Thông báo chính sách |

### 2.8. Đánh giá — `ReviewRequest` / `ReviewResponse`
| Thuộc tính | Kiểu | Nhãn | Ràng buộc |
|---|---|---|---|
| `bookingId` | UUID | Đơn được đánh giá | Bắt buộc |
| `accountId` | UUID | Tài khoản | Bắt buộc |
| `rating` | số nguyên | Số sao | 1–5 |
| `comment` | text | Nhận xét | ≤ 1000 ký tự |
| `reviewId` | UUID | Mã đánh giá | (phản hồi) |
| `createdAt` | datetime | Thời điểm đánh giá | (phản hồi) |

### 2.9. Thông tin tài khoản khách (hồ sơ) — `Account`
> *Đăng nhập/đăng ký do module Authentication (Quân) đảm nhận; ở đây liệt kê các
> trường liên quan để thiết kế màn hình hồ sơ/hiển thị tên khách.*

| Thuộc tính | Kiểu | Nhãn | Ràng buộc |
|---|---|---|---|
| `fullName` | text | Họ và tên | |
| `email` | text | Email | Đúng định dạng email |
| `phoneNumber` | text | Số điện thoại | Định dạng `^0\d{9}$` |
| `password` | text | Mật khẩu | (ẩn khi hiển thị) |
| `role` | Role | Vai trò | Khách hàng = `CUSTOMER` |
| `status` | AccountStatus | Trạng thái tài khoản | ACTIVE/INACTIVE/... |

---

## 3. QUY TẮC NGHIỆP VỤ CẦN THỂ HIỆN TRÊN UI

| # | Quy tắc | Thể hiện trên UI |
|---|---|---|
| R1 | Điểm đi ≠ điểm đến | Báo lỗi ngay tại form tìm kiếm |
| R2 | Ngày đi không được trong quá khứ | Khóa chọn ngày quá khứ trên date-picker |
| R3 | **Giữ ghế tạm 10 phút** | Đồng hồ **đếm ngược** ở màn đặt vé & thanh toán; hết giờ → thông báo "Hết thời gian giữ ghế, vui lòng đặt lại" |
| R4 | Tối đa **5 ghế/lần đặt** | Chặn chọn ghế thứ 6, hiển thị nhắc |
| R5 | Ghế vừa bị người khác giữ (race) | Khi đặt bị từ chối → hiện "Ghế X không còn trống", tự **làm mới sơ đồ ghế** |
| R6 | Chuyến đã khởi hành/không bán | Ẩn nút đặt hoặc báo "Chuyến đã khởi hành hoặc không còn bán vé" |
| R7 | Chính sách hoàn tiền (E5) | Hiển thị rõ trước khi xác nhận hủy (xem mục 4.5) |
| R8 | Không hủy khi chuyến đã khởi hành | Ẩn/disable nút Hủy |
| R9 | Chỉ đánh giá sau khi chuyến hoàn thành | Nút "Đánh giá" chỉ hiện với đơn đã đi xong |
| R10 | Không đánh giá trùng 1 đơn | Sau khi đánh giá → thay bằng "Đã đánh giá" |

**Cách tính tiền (để hiển thị minh bạch):**
- Giá vé = giá theo chặng trên tuyến (điểm xuống − điểm lên).
- Phí hành lý: **miễn phí 20kg đầu**, phần vượt tính **10.000đ/kg**.
- Thành tiền mỗi vé = Giá vé + Phí hành lý. Tổng đơn = tổng các vé.

---

## 4. ĐẶC TẢ TỪNG MÀN HÌNH

### 4.1. Màn E1 — Tìm kiếm chuyến  `GET /api/customer/trips`
**Mục đích:** khách nhập điểm đi/đến/ngày → xem danh sách chuyến.

**Thành phần:**
- Form tìm kiếm: **Điểm đi** (text/autocomplete), **Điểm đến** (text/autocomplete),
  **Ngày đi** (date-picker, chặn quá khứ), nút **Tìm chuyến**.
- Danh sách kết quả — mỗi card gồm: Điểm đi → Điểm đến, Giờ khởi hành, Nhà xe,
  **Số ghế trống**, nút **Chọn chuyến**.

**Trạng thái:**
- *Loading:* skeleton card.
- *Rỗng:* "Không tìm thấy chuyến phù hợp" + gợi ý đổi ngày/tuyến.
- *Lỗi R1/R2:* thông báo đỏ dưới ô tương ứng.

---

### 4.2. Màn E2 — Sơ đồ ghế & đặt vé
`GET /api/customer/trips/{tripId}/seats` → `POST /api/customer/bookings`

**Mục đích:** chọn ghế trống → nhập thông tin hành khách → giữ ghế tạm.

**Thành phần:**
- **Sơ đồ ghế** (xe 16 chỗ): lưới ghế theo mã `A01…`, tô màu theo `SeatStatus`
  (1.2). Ghế `free=false` **không bấm được**. Chú thích màu (legend).
- Khu **ghế đã chọn**: chip mã ghế, cho bỏ chọn. Bộ đếm "Đã chọn n/5".
- **Form hành khách** (mỗi ghế 1 dòng): Họ tên, Hành lý (kg).
- Chọn **Loại vé** (Một chiều/Khứ hồi), **Ghi chú** (tùy chọn).
- **Tóm tắt giá**: từng vé (giá vé + phí hành lý) và **Tổng tiền**.
- Nút **Đặt vé (giữ ghế)**.

**Trạng thái/tương tác:**
- Sau khi đặt thành công → chuyển sang màn thanh toán kèm **đồng hồ đếm ngược**
  tới `holdExpiresAt`.
- *R4:* chặn chọn quá 5 ghế.
- *R5:* nếu POST trả lỗi "Ghế X không còn trống" → toast đỏ + reload sơ đồ.
- *R6:* chuyến hết hạn bán → disable nút.

---

### 4.3. Màn E3 — Thanh toán  `POST /api/customer/bookings/{id}/pay`
**Mục đích:** xác nhận thanh toán cho đơn `PENDING_PAYMENT`.

**Thành phần:**
- Tóm tắt đơn: mã đơn, hành trình, giờ đi, danh sách vé/ghế, **Tổng tiền**.
- **Đồng hồ đếm ngược** thời gian giữ ghế còn lại.
- Chọn **Phương thức thanh toán** (1.4).
- Nút **Thanh toán**.

**Trạng thái:**
- *Thành công:* đơn → **Đã thanh toán (CONFIRMED)**, hiện màn xác nhận + mã vé.
- *Hết hạn giữ ghế (R3):* "Hết thời gian giữ ghế, vui lòng đặt lại" → về E1/E2.
- *Ghi chú:* đây là bản đặt tạm; cổng thanh toán thật sẽ bổ sung sau.

---

### 4.4. Màn E4 — Vé của tôi
`GET /api/customer/bookings?accountId=` (danh sách) ·
`GET /api/customer/bookings/{id}` (chi tiết)

**Thành phần — danh sách:**
- Bộ lọc theo trạng thái (tất cả / chờ thanh toán / đã thanh toán / đã hủy…).
- Mỗi dòng: mã đơn, hành trình, giờ đi, **badge trạng thái** (1.1), tổng tiền.

**Thành phần — chi tiết:**
- Thông tin hành trình + giờ khởi hành.
- Danh sách vé: ghế, hành khách, giá vé, phí hành lý, thành tiền.
- Nút hành động theo trạng thái:
  - `PENDING_PAYMENT` → **Thanh toán** (E3), **Hủy** (E5).
  - `CONFIRMED` (chưa khởi hành) → **Hủy** (E5).
  - Đơn đã đi xong & chưa đánh giá → **Đánh giá** (E6).

**Trạng thái rỗng:** "Bạn chưa có vé nào" + nút "Tìm chuyến".

---

### 4.5. Màn E5 — Hủy vé / Hoàn tiền  `POST /api/customer/bookings/{id}/cancel`
**Mục đích:** hủy đơn và hiển thị số tiền hoàn theo chính sách.

**Hộp thoại xác nhận — hiển thị rõ chính sách hoàn tiền:**
| Thời gian trước khởi hành | Mức hoàn |
|---|---|
| ≥ 24 giờ | Hoàn **100%** |
| ≥ 2 giờ | Hoàn **50%** |
| < 2 giờ | **Không hoàn** |

**Thành phần:** tóm tắt đơn, dòng "Số tiền dự kiến hoàn: …", nút **Xác nhận hủy** / **Đóng**.

**Trạng thái:**
- *Thành công:* hiện `message` + trạng thái mới (`CANCELLED` hoặc `REFUND_PENDING`).
- *R8:* chuyến đã khởi hành → không cho hủy, báo lỗi.
- Đơn đã hủy/đang hoàn → nút Hủy bị ẩn.

---

### 4.6. Màn E6 — Đánh giá chuyến  `POST /api/customer/reviews`
**Mục đích:** chấm sao + nhận xét sau chuyến đi.

**Thành phần:**
- Thông tin chuyến đã đi.
- **Chọn sao** (1–5, bắt buộc).
- **Nhận xét** (textarea, ≤ 1000 ký tự, đếm ký tự).
- Nút **Gửi đánh giá**.

**Trạng thái:**
- *R9:* chỉ mở được với đơn đã hoàn thành.
- *R10:* đã đánh giá → hiển thị đánh giá cũ, chặn gửi lại ("Đơn này đã được đánh giá").
- Đơn đã hủy/hoàn → không cho đánh giá.

---

## 5. GHI CHÚ CHUNG CHO THIẾT KẾ

- **Định dạng ngày giờ:** `dd/MM/yyyy HH:mm`. **Tiền tệ:** VND, phân tách hàng nghìn.
- **Badge trạng thái:** dùng bảng màu ở mục 1 cho nhất quán giữa các màn.
- **Thông báo lỗi:** hiển thị message từ BE (đã có sẵn tiếng Việt), dạng toast/inline.
- **Responsive:** ưu tiên mobile cho web khách hàng (đặt vé trên điện thoại).
- **Đồng hồ giữ ghế:** thành phần dùng lại ở E2 & E3, dựa trên `holdExpiresAt`.
- **Tài khoản:** hiện `accountId` truyền kèm request; khi có đăng nhập sẽ lấy từ
  phiên người dùng — thiết kế header nên có khu vực "Xin chào, {fullName}".

---

*Tài liệu này bám sát backend đã hiện thực (các DTO & API `/api/customer/**`) để
đảm bảo giao diện thiết kế ra khớp đúng dữ liệu và luồng thực tế của hệ thống.*
