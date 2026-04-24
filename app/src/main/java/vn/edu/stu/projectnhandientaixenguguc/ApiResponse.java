package vn.edu.stu.projectnhandientaixenguguc;

import vn.edu.stu.projectnhandientaixenguguc.entity.User;

public class ApiResponse {
    private int code;
    private String message;
    private AuthResult result;

    public int getCode() { return code; }

    public String getMessage() {
        return message;
    }

    public AuthResult getResult() { return result; }
}
