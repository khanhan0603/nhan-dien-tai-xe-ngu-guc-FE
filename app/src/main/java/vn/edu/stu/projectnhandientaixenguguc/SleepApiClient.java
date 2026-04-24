package vn.edu.stu.projectnhandientaixenguguc;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SleepApiClient {
    private static final String BASE_URL = "http:///10.248.223.31:8080/driverproject/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
