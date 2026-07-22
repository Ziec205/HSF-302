# Customer — Việt

Chức năng dành cho **Khách hàng** (mục E trong FunctionList).

| Mã | Chức năng | Trạng thái |
|---|---|---|
| E1 | Tìm kiếm chuyến (điểm đi, điểm đến, ngày) | ✅ Đang làm |
| E2 | Chọn ghế & đặt vé (giữ ghế tạm có timeout) | ✅ Đang làm |
| E3 | Thanh toán | ⏳ Placeholder (Payment làm sau) |
| E4 | Quản lý vé của tôi | ✅ Đang làm |
| E5 | Hủy vé / yêu cầu hoàn tiền | ✅ Đang làm |
| E6 | Đánh giá & phản hồi | ✅ Đang làm |

## Cấu trúc

```
Customer/
├── Controller/   REST controller cho web khách hàng
├── Service/      Nghiệp vụ Customer
└── Dto/          Request/Response DTO
```

## Quy ước API

Tất cả endpoint của Customer đặt dưới tiền tố `/api/customer/**`.
