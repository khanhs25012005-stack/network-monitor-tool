# Network Monitor Tool — Nhóm 5

Đồ án môn Lập trình mạng: giám sát kết nối IP/port, băng thông và tốc độ gửi/nhận; đo tốc độ truyền qua Socket giữa hai máy. Giao diện thống kê và biểu đồ là phần mở rộng bắt buộc của đề tài.

## Môi trường thống nhất

- JDK 17 trở lên; mã nguồn được biên dịch ở mức Java 17.
- Apache NetBeans 31, Maven 3.9.x; hệ điều hành thử nghiệm và đối chiếu là Windows 11.
- OSHI 7.6.1, JFreeChart 1.5.6, Gson 2.15.0. Phiên bản nằm trong `pom.xml`.

Mở thư mục chứa `pom.xml` bằng **File > Open Project** trong NetBeans. Chạy **Clean and Build**. Từ terminal có Maven:

```powershell
mvn clean package
java -cp target/classes com.nhom5.netmonitor.App
```

Hiện tại ứng dụng chỉ in thông báo xác nhận khung dự án chạy được; các module chức năng thuộc tuần 2–4.

## Phạm vi và cách làm việc

Xem [kiến trúc tuần 1](docs/KIEN_TRUC_TUAN_1.md), [quy trình Git](CONTRIBUTING.md) và [hướng dẫn các module](docs/HUONG_DAN_THANH_VIEN.md). Mỗi thành viên fork repo, làm trên nhánh của mình, tạo pull request và chờ Thịnh duyệt, merge. Không commit dữ liệu cá nhân, địa chỉ IP thật hoặc log thu thập từ máy thật.
