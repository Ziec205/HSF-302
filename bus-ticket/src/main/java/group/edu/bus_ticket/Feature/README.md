# Feature — Tổ chức theo VAI TRÒ (Role)

Thư mục `Feature` chứa tầng **ứng dụng + trình bày** (Controller / Service / DTO) được
tách riêng theo từng vai trò người dùng, để nhìn vào là biết ngay **thư mục nào của ai**
và **bên trong có chức năng gì**.

Các tầng dùng chung vẫn nằm ngoài `Feature`:

| Tầng | Vị trí | Mô tả |
|---|---|---|
| Domain (Entity / Enum / Repository port) | `Domain/` | Mô hình miền dùng chung |
| Persistence (JPA adapter) | `Infrastructure/Persistence/` | Cài đặt repository |
| Mapper | `Application/Mapper/` | MapStruct dùng chung |

## Phân chia theo role

| Thư mục | Người phụ trách | Phạm vi |
|---|---|---|
| [`Customer/`](Customer/README.md) | **Việt** | Tìm chuyến, đặt vé, thanh toán, vé của tôi, hủy/hoàn, đánh giá |
| [`Admin/`](Admin/README.md) | **Quân** | Quản trị hệ thống (+ Authentication chung) |
| [`Staff/`](Staff/README.md) | **Phong** | Bán vé, quản lý booking, check-in |
| [`Driver/`](Driver/README.md) | **An** | Chuyến được phân công, manifest, cập nhật trạng thái |

> Chi tiết nghiệp vụ: xem [Document/FunctionList.md](../../../../../../../Document/FunctionList.md).
