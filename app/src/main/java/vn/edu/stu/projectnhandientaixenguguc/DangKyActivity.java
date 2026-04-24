package vn.edu.stu.projectnhandientaixenguguc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class DangKyActivity extends AppCompatActivity {

    TextView tvLogin;
    Button btnDangKy;
    EditText edtTen,edtPhone,edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dangky);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private void addEvents() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
                startActivity(intent);
            }
        });
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname=edtTen.getText().toString().trim();
                String phone=edtPhone.getText().toString().trim();
                String password=edtPassword.getText().toString().trim();

                RegisterRequest request=new RegisterRequest(fullname,phone,password);

                ApiService apiService=ApiClient.getClient().create(ApiService.class);

                apiService.register(request).enqueue(new retrofit2.Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if(response.isSuccessful() && response.body() != null){

                            ApiResponse res = response.body();

                            if(res.getCode() == 1000){
                                Toast.makeText(DangKyActivity.this,
                                        "Đăng ký thành công!",
                                        Toast.LENGTH_LONG).show();

                                // 👉 chuyển sang màn hình đăng nhập
                                Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DangKyActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            try {
                                String errorJson = response.errorBody().string();
                                Log.e("API ERROR", errorJson);

                                // parse JSON lỗi
                                JSONObject json = new JSONObject(errorJson);
                                String message = json.getString("message");

                                handleError(message);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(DangKyActivity.this, "Server error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e("API ERROR", t.getMessage());
                        Toast.makeText(DangKyActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void handleError(String message) {
        if(message == null) return;

        if(message.contains("điện thoại")){
            edtPhone.setError(message);
            edtPhone.requestFocus();
        }
        else if(message.contains("Mật khẩu")){
            edtPassword.setError(message);
            edtPassword.requestFocus();
        }
        else if(message.contains("tên")){
            edtTen.setError(message);
            edtTen.requestFocus();
        }
        else{
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void addControls() {
        tvLogin=findViewById(R.id.tvLogin);
        btnDangKy=findViewById(R.id.btnDangKy);
        edtTen=findViewById(R.id.edtTen);
        edtPhone=findViewById(R.id.edtPhone);
        edtPassword=findViewById(R.id.edtPassword);
    }
}