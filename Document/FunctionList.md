# HỆ THỐNG ĐẶT VÉ XE BUS — DANH SÁCH NGHIỆP VỤ

> Tài liệu đặc tả nghiệp vụ (Business Requirements) cho hệ thống đặt vé xe bus.
> Bao gồm **Happy case** và các **trường hợp ngoại lệ** cho từng chức năng.

---

## MỤC LỤC

- [1. Kiến trúc tổng quan](#1-kiến-trúc-tổng-quan)
- [2. Vai trò người dùng](#2-vai-trò-người-dùng)
- [A. Nghiệp vụ chung — Authentication & Authorization](#a-nghiệp-vụ-chung--authentication--authorization)
- [B. Admin — Quản trị hệ thống](#b-admin--quản-trị-hệ-thống)
- [C. Staff — Nhân viên bán vé / hỗ trợ](#c-staff--nhân-viên-bán-vé--hỗ-trợ)
- [D. Driver — Tài xế](#d-driver--tài-xế)
- [E. Customer — Khách hàng](#e-customer--khách-hàng)
- [F. Nghiệp vụ hệ thống — Cross-cutting](#f-nghiệp-vụ-hệ-thống--cross-cutting)

---

## 1. KIẾN TRÚC TỔNG QUAN

| Thành phần | Mô tả |
|---|---|
| **Web quản trị** | Dùng chung cho Admin / Staff / Driver. Giao diện render theo role sau khi đăng nhập. |
| **Web khách hàng** | FE riêng biệt, phục vụ Customer. |
| **Frontend** | HTML + Thymeleaf |
| **Backend** | Java + Spring Boot |

**Nguyên tắc bảo mật quan trọng:** Backend **không tin tưởng Frontend**. Mọi kiểm tra phân quyền, hợp lệ dữ liệu đều thực hiện ở BE, kể cả khi FE đã ẩn/khóa chức năng.

---

## 2. VAI TRÒ NGƯỜI DÙNG

| Vai trò | Web | Vai trò chính |
|---|---|---|
| **Admin** | Quản trị | Quản lý toàn hệ thống: tài khoản, tuyến, xe, chuyến, giá, báo cáo |
| **Staff** | Quản trị | Bán vé, quản lý booking, check-in, hỗ trợ khách |
| **Driver** | Quản trị | Xem chuyến được phân công, danh sách khách, cập nhật trạng thái chuyến |
| **Customer** | Khách hàng | Tìm chuyến, đặt vé, thanh toán, quản lý vé |

---

## A. NGHIỆP VỤ CHUNG — AUTHENTICATION & AUTHORIZATION

### A1. Đăng nhập

**Happy case:** Người dùng nhập username/email + mật khẩu đúng → hệ thống xác thực → sinh session/JWT → điều hướng đến dashboard theo role (Admin/Staff/Driver về web quản trị; Customer về web khách hàng).

**Ngoại lệ:**
- Sai mật khẩu → báo lỗi chung, không tiết lộ trường nào sai.
- Sai quá số lần cho phép (VD 5 lần) → khóa tạm thời trong X phút (chống brute-force).
- Tài khoản bị vô hiệu hóa/khóa → chặn, hiển thị thông báo liên hệ hỗ trợ.
- Customer đăng nhập nhầm vào web quản trị (hoặc ngược lại) → chặn theo role, báo lỗi truy cập.
- Session hết hạn → tự động đăng xuất, chuyển về trang login.

### A2. Đăng ký (chỉ Customer)

**Happy case:** Customer nhập thông tin → hệ thống kiểm tra hợp lệ → gửi email/OTP xác thực → kích hoạt tài khoản.

**Ngoại lệ:**
- Email/SĐT đã tồn tại → báo trùng.
- Mật khẩu không đủ mạnh → yêu cầu nhập lại.
- OTP hết hạn hoặc sai → cho gửi lại, giới hạn số lần gửi.
- Bỏ dở giữa chừng → tài khoản ở trạng thái chờ kích hoạt, xác thực lại sau.

### A3. Quên / Đổi mật khẩu

**Happy case:** Nhập email → nhận link/OTP → đặt mật khẩu mới → đăng nhập lại.

**Ngoại lệ:**
- Email không tồn tại → thông báo trung lập (tránh dò tài khoản).
- Link reset hết hạn/đã dùng → yêu cầu gửi lại.
- Mật khẩu mới trùng mật khẩu cũ → cảnh báo (tùy chính sách).

### A4. Phân quyền (Authorization)

**Happy case:** Mỗi request được kiểm tra role trước khi cho phép truy cập chức năng/API.

**Ngoại lệ:**
- Truy cập chức năng vượt quyền → trả về `403 Forbidden`.
- Gọi API trực tiếp không qua UI → BE vẫn chặn theo role.

---

## B. ADMIN — QUẢN TRỊ HỆ THỐNG

### B1. Quản lý tài khoản người dùng

**Happy case:** Admin xem danh sách/tạo/sửa/khóa/mở tài khoản Staff, Driver, Customer; gán role.

**Ngoại lệ:**
- Tạo tài khoản trùng email/SĐT → chặn.
- Khóa Driver đang có chuyến được phân công → cảnh báo, yêu cầu xử lý chuyến trước.
- Tự khóa/xóa chính tài khoản Admin đang đăng nhập → chặn.
- Xóa tài khoản có dữ liệu liên quan → chuyển sang vô hiệu hóa (soft delete).

### B2. Quản lý tuyến đường (Route)

**Happy case:** Admin tạo tuyến (điểm đi, điểm đến, điểm dừng, khoảng cách, thời gian dự kiến), sửa, ngưng hoạt động.

**Ngoại lệ:**
- Điểm đi trùng điểm đến → chặn.
- Ngưng tuyến đang có chuyến/vé đã bán → chỉ vô hiệu hóa, không xóa cứng.
- Thứ tự điểm dừng không hợp lệ → yêu cầu chỉnh lại.

### B3. Quản lý xe bus (Bus)

**Happy case:** Thêm/sửa xe (biển số, số ghế, loại xe, sơ đồ ghế), đặt trạng thái (hoạt động/bảo trì).

**Ngoại lệ:**
- Biển số trùng → chặn.
- Chuyển xe sang bảo trì khi đang gán chuyến → cảnh báo, yêu cầu đổi xe.
- Sửa số ghế của xe đã có vé bán → chặn hoặc chỉ áp dụng cho chuyến tương lai.

### B4. Quản lý lịch trình / chuyến (Trip / Schedule)

**Happy case:** Admin tạo chuyến = tuyến + xe + tài xế + ngày giờ khởi hành + giá vé; hệ thống sinh danh sách ghế theo sơ đồ xe.

**Ngoại lệ:**
- Trùng lịch: 1 tài xế hoặc 1 xe gán 2 chuyến chồng thời gian → chặn.
- Giờ khởi hành trong quá khứ → chặn.
- Giá vé ≤ 0 → chặn.
- Hủy chuyến đã có vé → kích hoạt quy trình thông báo + hoàn tiền cho khách.

### B5. Quản lý giá vé & khuyến mãi

**Happy case:** Cấu hình giá theo tuyến/loại ghế; tạo mã giảm giá (điều kiện, hạn dùng, số lượng).

**Ngoại lệ:**
- Mã khuyến mãi trùng code → chặn.
- Hạn dùng trong quá khứ → chặn.
- % giảm > 100% hoặc âm → chặn.

### B6. Báo cáo & thống kê

**Happy case:** Xem doanh thu theo ngày/tuyến/chuyến, tỷ lệ lấp đầy ghế, top tuyến bán chạy; xuất báo cáo.

**Ngoại lệ:**
- Không có dữ liệu trong khoảng lọc → hiển thị "không có dữ liệu".
- Khoảng ngày không hợp lệ (từ > đến) → báo lỗi.

---

## C. STAFF — NHÂN VIÊN BÁN VÉ / HỖ TRỢ

### C1. Bán vé tại quầy / đặt vé hộ khách

**Happy case:** Staff chọn chuyến → chọn ghế trống → nhập thông tin khách → xác nhận thanh toán (tiền mặt/chuyển khoản) → xuất vé.

**Ngoại lệ:**
- Ghế vừa bị người khác giữ/đặt (race condition) → báo ghế không còn, làm mới sơ đồ.
- Chuyến đã đầy → chặn.
- Chuyến đã khởi hành/quá giờ bán → chặn.
- Khách hủy khi chưa thanh toán → giải phóng ghế đang giữ sau timeout.

### C2. Quản lý đặt vé (Booking)

**Happy case:** Staff tra cứu vé theo mã/SĐT/tên; xem chi tiết, đổi ghế, hủy vé, hoàn tiền theo chính sách.

**Ngoại lệ:**
- Hủy vé sát giờ khởi hành → áp dụng phí hủy/không hoàn tùy chính sách.
- Đổi sang ghế/chuyến đã đầy → chặn.
- Vé đã sử dụng (đã check-in) → không cho hủy/đổi.

### C3. Check-in hành khách

**Happy case:** Staff quét mã vé/nhập mã → xác nhận khách lên xe → cập nhật trạng thái vé "đã sử dụng".

**Ngoại lệ:**
- Vé sai chuyến/sai ngày → cảnh báo.
- Vé đã check-in trước đó → chặn check-in trùng.
- Vé đã bị hủy/hoàn → chặn.

### C4. Hỗ trợ & xử lý khiếu nại

**Happy case:** Staff tiếp nhận yêu cầu của khách, tra cứu, xử lý (đổi/hủy/hoàn), ghi log.

**Ngoại lệ:**
- Yêu cầu vượt quyền Staff (VD hoàn tiền lớn) → chuyển Admin duyệt.

---

## D. DRIVER — TÀI XẾ

### D1. Xem chuyến được phân công

**Happy case:** Driver đăng nhập → xem danh sách chuyến của mình theo ngày (tuyến, giờ, xe, điểm đón).

**Ngoại lệ:**
- Không có chuyến nào → hiển thị trạng thái trống.
- Chuyến bị hủy/đổi tài xế → cập nhật, thông báo.

### D2. Xem danh sách hành khách (Manifest)

**Happy case:** Driver xem danh sách khách của chuyến (tên, ghế, điểm lên/xuống, trạng thái check-in).

**Ngoại lệ:**
- Chưa đến giờ mở manifest → giới hạn xem trước X giờ (tùy chính sách).
- Danh sách thay đổi sau khi khách hủy/đặt thêm → làm mới dữ liệu.

### D3. Cập nhật trạng thái chuyến

**Happy case:** Driver cập nhật trạng thái: bắt đầu chạy → đang chạy → hoàn thành; hoặc điểm dừng đã qua.

**Ngoại lệ:**
- Cập nhật "hoàn thành" khi chưa "bắt đầu" → chặn (thứ tự trạng thái).
- Chuyến chưa đến giờ khởi hành → chặn bắt đầu sớm bất thường (tùy chính sách).
- Báo sự cố (hỏng xe, tai nạn) → gửi cảnh báo cho Staff/Admin để điều phối.

### D4. Check-in hành khách (nếu được phân quyền)

**Happy case:** Driver quét mã vé xác nhận khách lên xe.

**Ngoại lệ:** Tương tự C3 (vé sai chuyến, đã check-in, đã hủy).

---

## E. CUSTOMER — KHÁCH HÀNG

### E1. Tìm kiếm chuyến

**Happy case:** Khách nhập điểm đi, điểm đến, ngày → hệ thống trả về danh sách chuyến khả dụng (giờ, giá, số ghế trống).

**Ngoại lệ:**
- Không có chuyến phù hợp → gợi ý ngày khác/tuyến gần.
- Ngày trong quá khứ → chặn.
- Điểm đi = điểm đến → báo lỗi.

### E2. Chọn ghế & đặt vé

**Happy case:** Khách chọn chuyến → xem sơ đồ ghế → chọn ghế trống → nhập thông tin hành khách → hệ thống giữ ghế tạm (timeout).

**Ngoại lệ:**
- Ghế vừa bị người khác chọn → thông báo, làm mới sơ đồ.
- Vượt số ghế tối đa cho 1 lần đặt → chặn.
- Hết thời gian giữ ghế trước khi thanh toán → giải phóng ghế, thông báo hết hạn.

### E3. Thanh toán

**Happy case:** Khách chọn phương thức (ví điện tử/thẻ/chuyển khoản) → thanh toán thành công → xác nhận vé + gửi email/SMS mã vé.

**Ngoại lệ:**
- Thanh toán thất bại → giữ đơn ở trạng thái chờ, cho thử lại trong thời gian giữ ghế.
- Timeout cổng thanh toán → đối soát, tránh trừ tiền mà không xuất vé (idempotency).
- Trừ tiền thành công nhưng lỗi sinh vé → cơ chế đối soát + hoàn tiền/cấp vé bù.
- Áp mã khuyến mãi hết hạn/không đủ điều kiện → báo lỗi, giữ giá gốc.

### E4. Quản lý vé của tôi

**Happy case:** Khách xem lịch sử/vé sắp đi; xem chi tiết mã vé, giờ, ghế.

**Ngoại lệ:**
- Không có vé → hiển thị trạng thái trống.

### E5. Hủy vé / Yêu cầu hoàn tiền

**Happy case:** Khách hủy vé trong thời hạn cho phép → hệ thống tính phí hủy → hoàn tiền theo chính sách → giải phóng ghế.

**Ngoại lệ:**
- Hủy quá sát giờ khởi hành → không cho hủy hoặc không hoàn.
- Vé đã check-in/đã sử dụng → chặn hủy.
- Chuyến bị nhà xe hủy → hoàn 100%, không tính phí, thông báo chủ động.

### E6. Đánh giá & phản hồi

**Happy case:** Sau chuyến đi, khách đánh giá (sao, nhận xét) về chuyến/tài xế.

**Ngoại lệ:**
- Đánh giá khi chưa hoàn thành chuyến → chặn.
- Đánh giá trùng cho cùng 1 vé → chặn.

---

## F. NGHIỆP VỤ HỆ THỐNG — CROSS-CUTTING

| Nghiệp vụ | Mô tả |
|---|---|
| **Giữ ghế tạm & timeout** | Lock ghế khi khách/Staff đang đặt; tự giải phóng sau X phút nếu chưa thanh toán. Xử lý concurrency để 2 người không đặt trùng 1 ghế. |
| **Idempotency thanh toán** | Đảm bảo 1 giao dịch không bị tính/xuất vé 2 lần khi retry. |
| **Thông báo** | Email/SMS khi đặt thành công, thay đổi/hủy chuyến, nhắc trước giờ khởi hành. |
| **Soft delete** | Dữ liệu có liên kết (tuyến, xe, tài khoản) chỉ vô hiệu hóa, không xóa cứng, để bảo toàn lịch sử. |
| **Audit log** | Ghi lại thao tác quan trọng (hủy vé, hoàn tiền, đổi lịch) kèm người thực hiện. |

---

## PHỤ LỤC — TRẠNG THÁI ĐỐI TƯỢNG (gợi ý)

**Trạng thái vé (Ticket):** `PENDING` → `PAID` → `CHECKED_IN` → `USED` / `CANCELLED` / `REFUNDED`

**Trạng thái chuyến (Trip):** `SCHEDULED` → `BOARDING` → `IN_PROGRESS` → `COMPLETED` / `CANCELLED`

**Trạng thái ghế (Seat):** `AVAILABLE` → `HELD` (giữ tạm) → `BOOKED`

**Trạng thái tài khoản (Account):** `ACTIVE` / `INACTIVE` / `LOCKED` / `PENDING_ACTIVATION`
