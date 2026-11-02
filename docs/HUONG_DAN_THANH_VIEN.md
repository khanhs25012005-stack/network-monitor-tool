# Kiến thức để hỗ trợ công việc các thành viên

## Nguyễn Quang Huy — OSHI và băng thông

Từ `SystemInfo`, lấy `HardwareAbstractionLayer#getNetworkIFs()` để duyệt card. Mỗi card `NetworkIF` có `getBytesRecv()`, `getBytesSent()`, `getSpeed()` và `updateAttributes()`. `getSpeed()` là tốc độ danh định của liên kết (bit/giây), không phải lưu lượng hiện tại. Lấy mẫu 2 lần, cách nhau khoảng 1 giây, gọi `updateAttributes()` trước mỗi lần đọc. Tính `recvBytesPerSec = (recv2 - recv1) * 1000 / elapsedMillis`; tương tự cho gửi. Tỷ lệ sử dụng xấp xỉ `8 * bytesPerSec / speed * 100`, chỉ tính khi speed > 0. Nếu counter giảm, thời gian không tăng, card biến mất hoặc đổi ID thì bỏ mẫu đầu và lấy lại mốc. Không cập nhật Swing từ thread này. Đối chiếu cùng card và cùng cửa sổ thời gian với Task Manager; ghi sai lệch và cách làm tròn. Demo tuần 1 chỉ cần in tên card, byte gửi/nhận và tốc độ mỗi giây ra console.

## Nguyễn Duy Tiến — Swing và biểu đồ

Wireframe nên có vùng chọn card mạng, bảng kết nối (giao thức, local IP/port, remote IP/port, trạng thái, PID), biểu đồ tốc độ gửi/nhận và trang thống kê. `JTable` nên nhận dữ liệu qua `AbstractTableModel`; khi snapshot mới đến, thay mô hình trên EDT bằng `SwingUtilities.invokeLater`. Dữ liệu lịch sử giữ giới hạn, ví dụ 300 mẫu, để biểu đồ không tăng bộ nhớ mãi. JFreeChart hỗ trợ `TimeSeries`, `TimeSeriesCollection` và `ChartPanel`; cập nhật dataset trên EDT. Tuần 1 nộp ảnh phác thảo hoặc bản vẽ có nhãn trường, luồng chuyển màn hình, cùng danh sách thư viện; chưa cần UI chạy thật.

## Bùi Khánh Huyền — Socket, giao thức và kiểm thử

`ServerSocket#accept()` chờ kết nối; giao mỗi client cho một worker trong `ExecutorService`, đặt giới hạn số worker và timeout đọc. Agent dùng `Socket` kết nối tới Server; dùng UTF-8 và `BufferedReader#readLine()`/`BufferedWriter#newLine()` để đóng khung mỗi bản tin JSON trên một dòng. Định nghĩa mẫu `type`, `version`, `agentId`, `timestamp`, `payload`; cần thêm quy tắc giới hạn độ dài, trường bắt buộc và phản hồi lỗi. Một dòng JSON không được chứa newline thô bên trong chuỗi. Demo tuần 1: Server nhận chuỗi, phản hồi, Agent in kết quả; thử ngắt client giữa chừng. Đóng tài nguyên bằng try-with-resources. Từ tuần 2 viết test case nhiều client, timeout, JSON sai và mất kết nối; từ tuần 3 đo MB/s = bytes / seconds / 1_000_000 và độ trễ bằng thời gian khứ hồi, ghi rõ đây là phép đo ứng dụng chứ không phải tốc độ card mạng.

## Thịnh — ghép các module

Giữ dữ liệu chung trong `core`, cho từng monitor phát snapshot qua listener; `App` tạo và dừng dịch vụ. Không để UI phụ thuộc vào lớp nội bộ của OSHI hay giao thức Socket. Khi ghép PR, chạy build, kiểm tra thread kết thúc, lỗi quyền đọc PID, card đổi trạng thái và ảnh hưởng của dữ liệu rỗng. Tuần 2 mới triển khai `ConnectionMonitor` với `InternetProtocolStats` của OSHI; nếu API hoặc quyền truy cập không đáp ứng, dùng `netstat -ano` qua `ProcessBuilder` trên Windows và phân tích đầu ra cẩn thận.
