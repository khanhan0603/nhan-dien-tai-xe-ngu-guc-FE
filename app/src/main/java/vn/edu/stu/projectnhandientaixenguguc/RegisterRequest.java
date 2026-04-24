package vn.edu.stu.projectnhandientaixenguguc;

public class RegisterRequest {
    private String fullname;
    private String phone;
    private String password;

    public RegisterRequest(String fullname, String phone, String password) {
        this.fullname = fullname;
        this.phone = phone;
        this.password = password;
    }
}
