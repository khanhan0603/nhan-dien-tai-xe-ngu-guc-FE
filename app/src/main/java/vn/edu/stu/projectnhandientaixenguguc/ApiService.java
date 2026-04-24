package vn.edu.stu.projectnhandientaixenguguc;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import vn.edu.stu.projectnhandientaixenguguc.entity.SleepRecord;
import vn.edu.stu.projectnhandientaixenguguc.entity.User;

public interface ApiService {
    @POST("users")
    Call<ApiResponse> register(@Body RegisterRequest request);

    @POST("auth/token")
    Call<ApiResponse> login(@Body LoginRequest request);

    @GET("users/me")
    Call<UserResponse> getMyInfo(@Header("Authorization") String token);

    @POST("records")
    Call<SleepRecord> saveSleep(@Header("Authorization") String token,@Body SleepRecord record);

    @GET("records")
    Call<List<SleepRecord>> getSleepById(@Header("Authorization") String token);
}
