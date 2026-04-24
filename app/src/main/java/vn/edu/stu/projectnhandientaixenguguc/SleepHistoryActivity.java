package vn.edu.stu.projectnhandientaixenguguc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.stu.projectnhandientaixenguguc.entity.SleepRecord;

public class SleepHistoryActivity extends AppCompatActivity {
    ListView lvHistory;
    TextView tvClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sleep_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        loadData();
        addEvents();
    }

    private void loadData() {
        ApiService apiService=SleepApiClient.getClient().create(ApiService.class);
        //Lấy userId từ login
        SharedPreferences preferences=getSharedPreferences("USER",MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        //Debug
        Log.d("TOKEN", token);

        // Thêm "Bearer "
        String authHeader = "Bearer " + token;

        apiService.getSleepById(authHeader).enqueue(new Callback<List<SleepRecord>>() {
            @Override
            public void onResponse(Call<List<SleepRecord>> call, Response<List<SleepRecord>> response) {
                if (!response.isSuccessful()) {
                    Log.e("API", "Response error: " + response.code());
                    return;
                }

                List<SleepRecord> list = response.body();

                if (list == null) {
                    Log.e("API", "List NULL từ server");
                    Toast.makeText(SleepHistoryActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> display = new ArrayList<>();

                for (SleepRecord r : list) {
                    display.add(r.getTime());
                }
                SleepAdapter adapter=new SleepAdapter(SleepHistoryActivity.this,list);
                lvHistory.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<SleepRecord>> call, Throwable t) {
                Log.e("API",t.getMessage());
                Toast.makeText(SleepHistoryActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEvents() {
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SleepHistoryActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addControls() {
        lvHistory=findViewById(R.id.lvHistory);
        tvClose=findViewById(R.id.tvClose);
    }
}