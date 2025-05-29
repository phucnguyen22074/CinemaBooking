CinemaBooking - Hệ thống đặt vé xem phim
CinemaBooking là một ứng dụng web cho phép người dùng đặt vé xem phim trực tuyến và quản lý hệ thống rạp chiếu phim. Ứng dụng hỗ trợ người dùng thông thường với các tính năng như đăng nhập, đăng ký, lọc phim/rạp, chọn ghế, mua vé, xem lịch sử giao dịch, và vai trò admin với quyền quản lý dữ liệu thông qua các chức năng CRUD. Đồ án được xây dựng bằng Spring Boot, cung cấp trải nghiệm mượt mà và hiệu quả.
Tính năng chính
Tính năng dành cho người dùng

Đăng nhập/Đăng ký: Tạo tài khoản hoặc đăng nhập để sử dụng hệ thống.
Cập nhật tài khoản: Chỉnh sửa thông tin cá nhân như tên, email, mật khẩu.
Lọc rạp chiếu và phim: Tìm kiếm rạp chiếu và phim theo vị trí, thể loại, hoặc thời gian chiếu.
Chọn ghế và mua vé: Giao diện trực quan để chọn ghế và hoàn tất thanh toán vé xem phim.
Lịch sử mua vé: Xem danh sách vé đã mua, bao gồm thông tin phim, rạp, ghế, và thời gian.

Tính năng dành cho admin

Đăng nhập bằng tài khoản admin: Sử dụng tài khoản admin để truy cập giao diện quản trị.
Quản lý CRUD:
Users: Quản lý thông tin người dùng (tạo, xem, sửa, xóa).
Theaters: Quản lý danh sách rạp chiếu phim (địa điểm, thông tin liên hệ).
Screens: Quản lý các phòng chiếu trong rạp (số phòng, sức chứa).
Seats: Quản lý ghế ngồi trong mỗi phòng chiếu (vị trí, trạng thái).
Roles: Quản lý vai trò người dùng (admin, user, v.v.).
Movies: Quản lý thông tin phim (tên, thời lượng, ngày phát hành).
Genres: Quản lý thể loại phim (hành động, tình cảm, kinh dị, v.v.).
Director: Quản lý thông tin đạo diễn.
Brands: Quản lý các thương hiệu rạp chiếu (CGV, Lotte, v.v.).
Actor: Quản lý thông tin diễn viên.



Công nghệ sử dụng

Back-end: Spring Boot, Spring Security (xác thực và phân quyền), Spring Data JPA.
Front-end: Thymeleaf (hoặc React/Angular nếu bạn sử dụng), Bootstrap.
Cơ sở dữ liệu: MySQL/PostgreSQL (hoặc H2 để phát triển).
Công cụ: Maven, Git, IntelliJ IDEA.

Yêu cầu hệ thống

Java 17 hoặc cao hơn.
Maven 3.6+.
MySQL/PostgreSQL (tùy cấu hình).
Trình duyệt web hiện đại (Chrome, Firefox, Edge).

Hướng dẫn cài đặt

Clone dự án:git clone https://github.com/phucnguyen22074/CinemaBooking.git
cd CinemaBooking


Cấu hình cơ sở dữ liệu:
Tạo database, ví dụ: cinema_booking.
Cập nhật thông tin database trong file src/main/resources/application.properties:spring.datasource.url=jdbc:mysql://localhost:3306/cinema_booking
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update




Cài đặt phụ thuộc:mvn clean install


Chạy ứng dụng:mvn spring-boot:run


Truy cập ứng dụng tại: http://localhost:8089.

Hướng dẫn sử dụng
Người dùng thông thường

Đăng ký/Đăng nhập: Truy cập trang chủ, nhấp vào "Đăng ký" để tạo tài khoản hoặc "Đăng nhập" để vào hệ thống.
Lọc phim/rạp: Sử dụng bộ lọc để chọn phim hoặc rạp theo ý muốn.
Chọn ghế: Chọn suất chiếu, sau đó chọn ghế từ sơ đồ ghế hiển thị.
Mua vé: Xác nhận thông tin và hoàn tất thanh toán.
Lịch sử giao dịch: Vào mục "Lịch sử mua vé" để xem các vé đã đặt.

Admin

Đăng nhập admin: Sử dụng tài khoản admin (liên hệ người phát triển để lấy thông tin).
Quản lý dữ liệu:
Truy cập giao diện quản trị (/admin) để thực hiện các thao tác CRUD.
Quản lý các bảng: Users, Theaters, Screens, Seats, Roles, Movies, Genres, Director, Brands, Actor thông qua các biểu mẫu hoặc bảng điều khiển.



Cấu trúc cơ sở dữ liệu
Danh sách các bảng chính trong hệ thống:

users: Lưu thông tin người dùng (tên, email, mật khẩu, vai trò).
theaters: Lưu thông tin rạp chiếu (tên, địa chỉ, thương hiệu).
screens: Lưu thông tin phòng chiếu (số phòng, rạp, sức chứa).
seats: Lưu thông tin ghế (vị trí, trạng thái, phòng chiếu).
roles: Lưu vai trò người dùng (admin, user).
movies: Lưu thông tin phim (tên, thời lượng, đạo diễn, thể loại).
genres: Lưu thể loại phim.
director: Lưu thông tin đạo diễn.
brands: Lưu thương hiệu rạp chiếu.
actor: Lưu thông tin diễn viên.

Lưu ý

Bảo mật: Không đẩy file application.properties chứa thông tin nhạy cảm lên GitHub. Sử dụng application.properties.example làm mẫu.
Tài khoản admin: Tài khoản admin mặc định (nếu có) cần được cấu hình trước khi sử dụng.
Lỗi thường gặp: Kiểm tra log trong console hoặc file application.log nếu gặp sự cố khi chạy.

Đóng góp

Fork repository, tạo nhánh mới, và gửi pull request để đóng góp.
Báo lỗi hoặc đề xuất tính năng qua mục Issues trên GitHub.

Liên hệ

Tác giả: Phúc Nguyễn
Email: your-email@example.com
GitHub: phucnguyen22074

