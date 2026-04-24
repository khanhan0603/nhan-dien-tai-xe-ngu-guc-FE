package vn.edu.stu.projectnhandientaixenguguc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import vn.edu.stu.projectnhandientaixenguguc.entity.User;

public class HomeActivity extends AppCompatActivity {
    TextView tvTen;
    CardView cardStart,cardHistory,cardSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        loadUser();
        addEvents();
    }

    private void loadUser() {
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

                    tvTen.setText(user.getFullname());

                } else {
                    Toast.makeText(HomeActivity.this, "Không lấy được user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API ERROR", t.getMessage());
                Toast.makeText(HomeActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addControls() {
        tvTen=findViewById(R.id.tvTen);
        cardStart=findViewById(R.id.cardStart);
        cardHistory=findViewById(R.id.cardHistory);
        cardSetting=findViewById(R.id.cardSetting);
    }

    private void addEvents() {
        cardStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CameraActivity.class));
            }
        });
        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SleepHistoryActivity.class));
            }
        });
        cardSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,InformationActivity.class));
            }
        });
    }
}