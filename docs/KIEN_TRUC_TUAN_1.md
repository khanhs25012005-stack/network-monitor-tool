# Kiến trúc tuần 1 — Network Monitor Tool

**Phạm vi.** Ứng dụng Java trên Windows hiển thị IP, port, trạng thái kết nối TCP và cổng TCP/UDP đang lắng nghe; đo tốc độ gửi/nhận, tổng byte, tỷ lệ sử dụng băng thông; đo tốc độ truyền dữ liệu giữa Agent và Server qua Socket. Giao diện thống kê và biểu đồ thời gian thực là phần mở rộng bắt buộc. Xuất file, tên tiến trình và cảnh báo là ưu tiên sau khi phần chính ổn định.

**Môi trường.** JDK 17+, NetBeans 31, Maven 3.9.x, Windows 11. Thư viện: OSHI 7.6.1 để đọc card mạng và kết nối, JFreeChart 1.5.6 để vẽ biểu đồ, Gson 2.15.0 cho JSON một dòng. `pom.xml` là nguồn duy nhất cho phiên bản thư viện.

**Ranh giới module.** `core` giữ các bản ghi dữ liệu bất biến và listener; `monitor.connection` quét kết nối rồi phát danh sách IP/port/trạng thái/PID; `monitor.bandwidth` lấy mẫu byte của từng card mạng và phát tốc độ; `network` chứa Agent/Server, giao thức JSON một dòng và phép đo truyền; `ui` nhận sự kiện, hiển thị Swing/JFreeChart. `App` khởi tạo và quản lý vòng đời các module. Giao diện không gọi OSHI hay Socket trực tiếp.

**Luồng dữ liệu.** Mỗi monitor chạy trên thread nền, đọc số liệu định kỳ và gửi snapshot cho listener. UI chuyển cập nhật sang Event Dispatch Thread bằng `SwingUtilities.invokeLater`; không chặn EDT bằng I/O. Khi dừng, interrupt worker, đóng socket/server socket và chờ thread kết thúc. Chênh lệch counter và thời gian thực tế giữa hai mẫu tạo tốc độ byte/giây; counter giảm hoặc đổi card thì bắt đầu mẫu mới.

**Hợp đồng tích hợp.** Dữ liệu kết nối có IP/port cục bộ và từ xa, giao thức TCP/UDP, trạng thái, PID; PID hoặc địa chỉ không đọc được phải có giá trị vắng mặt rõ ràng. Dữ liệu băng thông có mã card, thời điểm, tổng byte gửi/nhận, byte/giây và tốc độ danh định của card (nếu có). Giao thức Agent/Server dùng UTF-8, một đối tượng JSON trên một dòng, có `type`, `version`, `agentId`, `timestamp` và `payload`; dòng lỗi hoặc quá dài được từ chối mà không làm sập Server. Chi tiết trường sẽ chốt cùng Huyền trước khi code tuần 2.

**Kiểm chứng.** Chạy từng module độc lập trước khi tích hợp. So sánh byte/giây với Task Manager/Resource Monitor trong cùng khoảng lấy mẫu; thử ngắt card, client rớt mạng và nhiều client. Khi demo, che tên máy và IP cá nhân trong ảnh. Mã test và bug theo `TC-xx`, `BUG-xx`.
