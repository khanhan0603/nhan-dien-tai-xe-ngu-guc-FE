package vn.edu.stu.projectnhandientaixenguguc.entity;

public class SleepRecord {
    private String time;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SleepRecord(String time, int count, String userId) {
        this.time = time;
        this.userId=userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
