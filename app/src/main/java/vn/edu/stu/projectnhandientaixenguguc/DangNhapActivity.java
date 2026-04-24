package vn.edu.stu.projectnhandientaixenguguc;

import static android.provider.Settings.System.putLong;

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
import retrofit2.Callback;
import retrofit2.Response;

public class DangNhapActivity extends AppCompatActivity {
    TextView tvSignUp;
    EditText edtPhone,edtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }

    private void addEvents() {
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhapActivity.this, DangKyActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=edtPassword.getText().toString().trim();
                String phone=edtPhone.getText().toString().trim();

                LoginRequest request=new LoginRequest(phone,password);

                ApiService apiService=ApiClient.getClient().create(ApiService.class);

                apiService.login(request).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if(response.isSuccessful()&&response.body()!=null) {
                            ApiResponse res = response.body();
                            if (res.getCode() == 0) {
                                Toast.makeText(DangNhapActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();

                                // lấy token
                                String token = res.getResult().getToken();
                                // LẤY userId từ token
                                String userId = getUserIdFromToken(token);

                                // lưu
                                getSharedPreferences("USER", MODE_PRIVATE)
                                        .edit()
                                        .putString("TOKEN", token)
                                        .putString("userId", userId)
                                        .apply();

                                // chuyển sang Home
                                Intent intent = new Intent(DangNhapActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DangNhapActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            try{
                                String errorJson = response.errorBody().string();
                                Log.e("API ERROR",errorJson);

                                JSONObject jsonObject=new JSONObject(errorJson);
                                String message= jsonObject.getString("message");

                                handleError(message);

                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(DangNhapActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e("API ERROR", t.getMessage());
                        Toast.makeText(DangNhapActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = parts[1];

            byte[] decoded = android.util.Base64.decode(payload, android.util.Base64.URL_SAFE);
            String json = new String(decoded);

            JSONObject obj = new JSONObject(json);
            return obj.getString("sub");

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void handleError(String message) {
        if(message==null) return;

        if(message.contains("Số điện thoại")){
            edtPhone.setError(message);
            edtPhone.requestFocus();
        }
        else if(message.contains("Mật khẩu")){
            edtPassword.setError(message);
            edtPassword.requestFocus();
        }
        else{
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void addControls() {
        tvSignUp=findViewById(R.id.tvSignUp);
        btnLogin=findViewById(R.id.btnLogin);
        edtPhone=findViewById(R.id.edtPhone);
        edtPassword=findViewById(R.id.edtPassword);
    }
}