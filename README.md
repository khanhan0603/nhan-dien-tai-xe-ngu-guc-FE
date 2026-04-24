# Driver Drowsiness Detection App (Android Client)

## Giới thiệu
Đây là ứng dụng Android đóng vai trò **client (frontend)** trong hệ thống phát hiện tài xế ngủ gật.

Ứng dụng thực hiện:
- Nhận diện trạng thái buồn ngủ trực tiếp trên thiết bị (AI module local)
- Cảnh báo người dùng theo thời gian thực
- Gửi dữ liệu lịch sử về Backend thông qua REST API

---

## Kiến trúc
- Ứng dụng Android đóng vai trò **Frontend (Client)**
- Module AI chạy **local trên thiết bị**
- Backend (repo riêng) chịu trách nhiệm:
  - Lưu trữ dữ liệu
  - Xử lý nghiệp vụ
  - Cung cấp API

---

## Chức năng
- Nhận diện ngủ gật qua camera
- Hiển thị cảnh báo khi phát hiện bất thường
- Gửi dữ liệu lịch sử ngủ gật lên server
- Gọi API từ Backend

---

## Công nghệ
- Java 21
- Android SDK
- RESTful API (HTTP)
- Git, GitHub

---

## CI (GitHub Actions)
Dự án sử dụng GitHub Actions để:
- Tự động build project khi push code
- Kiểm tra lỗi build sớm

---

## Chạy project

### Clone
```bash
git clone https://github.com/khanhan0603/nhan-dien-tai-xe-ngu-guc-FE.git
```
### Build
- Mở bằng Android Studio
- Chạy trên emulator hoặc thiết bị thật

---

## Backend
- Backend được phát triển ở repository riêng (https://github.com/khanhan0603/nhan-dien-tai-xe-ngu-guc-BE.git).
- Ứng dụng sử dụng API để gửi dữ liệu và nhận phản hồi từ hệ thống.
