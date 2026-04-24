package vn.edu.stu.projectnhandientaixenguguc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import vn.edu.stu.projectnhandientaixenguguc.entity.SleepRecord;

public class SleepAdapter extends ArrayAdapter<SleepRecord> {

    private final Context context;
    private final List<SleepRecord> list;

    public SleepAdapter(@NonNull Context context, @NonNull List<SleepRecord> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_text, parent, false);
        }

        // Striped rows: hàng chẵn trắng, hàng lẻ xám nhạt
        if (position % 2 == 0) {
            convertView.setBackgroundColor(0xFFFFFFFF);
        } else {
            convertView.setBackgroundColor(0xFFF6FAF7);
        }

        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvTime = convertView.findViewById(R.id.tvTime);

        SleepRecord r = list.get(position);

        // r.getTime() trả về "dd/MM/yyyy HH:mm:ss"
        // Tách ra 2 phần: ngày và giờ
        String fullTime = r.getTime();
        if (fullTime != null && fullTime.contains(" ")) {
            String[] parts = fullTime.split(" ");
            tvDate.setText(parts[0]); // dd/MM/yyyy
            tvTime.setText(parts[1]); // HH:mm:ss
        } else {
            tvDate.setText(fullTime);
            tvTime.setText("");
        }

        return convertView;
    }
}