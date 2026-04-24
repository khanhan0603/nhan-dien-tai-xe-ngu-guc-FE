package vn.edu.stu.projectnhandientaixenguguc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.processing.SurfaceProcessorNode;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.stu.projectnhandientaixenguguc.entity.SessionManager;

public class InformationActivity extends AppCompatActivity {
    EditText edtTen,edtPhone,edtPassword;
    Button btnDangXuat,btnThoat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        loadData();
        addEvents();
    }


    private void addEvents() {
        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager.logout(InformationActivity.this);

                Toast.makeText(InformationActivity.this, "Đăng xuất thành công!", Toast.LENGTH_LONG).show();

                Intent intent=new Intent(InformationActivity.this,DangNhapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(InformationActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadData() {
        // lấy token đã lưu
        SharedPreferences prefs = getSharedPreferences("USER", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Thêm "Bearer "
        String authHeader = "Bearer " + token;

        apiService.getMyInfo(authHeader).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    UserResponse user = response.body();

                    edtTen.setText(user.getFullname());
                    edtPhone.setText(user.getPhone());

                } else {
                    Toast.makeText(InformationActivity.this, "Không lấy được user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API ERROR", t.getMessage());
                Toast.makeText(InformationActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addControls() {
        edtTen=findViewById(R.id.edtTen);
        edtPhone=findViewById(R.id.edtPhone);
        edtPassword=findViewById(R.id.edtPassword);
        btnDangXuat=findViewById(R.id.btnDangXuat);
        btnThoat=findViewById(R.id.btnThoat);
    }
}