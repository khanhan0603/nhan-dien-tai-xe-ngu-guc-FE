package vn.edu.stu.projectnhandientaixenguguc;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
//    10.0.2.2:8080: may ảo
    private static final String BASE_URL = "http://10.248.223.31:8080/driverproject/";

    public static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


}
