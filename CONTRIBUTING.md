# Quy trình làm việc nhóm

Nhánh mặc định là `main`. Repo nằm trong tài khoản cá nhân của Thịnh. Thành viên fork repo, tạo nhánh trong fork từ `main`: `feature/quang-huy-<ten-viec>`, `feature/tien-<ten-viec>`, `feature/huyen-<ten-viec>`. Thịnh dùng `feature/thinh-<ten-viec>` trong repo chính. Không phát triển trực tiếp trên `main`.

Một pull request chỉ tập trung vào một module hoặc thay đổi liên quan. Commit nhỏ, theo mẫu `type(scope): mô tả ngắn`, ví dụ `feat(bandwidth): lấy mẫu byte mỗi giây`, `fix(network): đóng socket khi client ngắt`, `docs: cập nhật giao thức`. Trước khi mở pull request, chạy `mvn clean package`, ghi cách chạy thử và kết quả vào phần mô tả PR. Thịnh kiểm tra và là người duy nhất merge vào `main`.

Trong tuần 1, Huy làm khảo sát OSHI và demo console; Tiến làm wireframe; Huyền làm mô tả giao thức cùng demo TCP. Các kết quả được nộp qua pull request, chưa sửa mã của module khác. Mã test case dùng `TC-01`, `TC-02`; mã lỗi dùng `BUG-01`, `BUG-02`.

**Quyền trên GitHub:** `CODEOWNERS` tự động yêu cầu Thịnh review nhưng không giới hạn người bấm Merge. Chỉ Thịnh giữ quyền Write/Admin ở repo chính; thành viên dùng fork để đóng góp, không được cấp Write/Admin ở repo chính. Quy tắc bảo vệ `main` yêu cầu pull request, chặn force push và xóa nhánh. Với repo thuộc tài khoản cá nhân, đây là cách bảo đảm chỉ chủ repo có quyền merge. Nếu sau này chuyển repo sang organization và muốn thành viên push nhánh trực tiếp vào repo chung, phải thiết lập thêm branch restriction cho `main` chỉ cho phép Thịnh cập nhật.
