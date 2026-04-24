package vn.edu.stu.projectnhandientaixenguguc;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;
import android.graphics.Bitmap;
import android.graphics.Rect;
import org.tensorflow.lite.Interpreter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.stu.projectnhandientaixenguguc.entity.SleepRecord;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    Button btnTatBaoThuc;
    TextView tvClose;
    private ExecutorService cameraExecutor;
    private MediaPlayer mediaPlayer;
    private FaceDetector detector;

    private Interpreter tflite;

    private int eyeClosedCounter = 0;
    private static final int THRESHOLD = 3;

    private boolean isProcessing = false; // chống crash ML Kit
    private long lastTime = 0;            // giảm tần suất xử lý
    private boolean isAlarmPlaying = false;
    private boolean isSaved = false; //chống spam API
    private boolean isActive = true;

    @ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        btnTatBaoThuc=findViewById(R.id.btnTatBaoThuc);
        tvClose=findViewById(R.id.tvClose);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        // ML Kit config chuẩn
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // thêm
                        .enableTracking()
                        .build();

        detector = FaceDetection.getClient(options);
        cameraExecutor = Executors.newSingleThreadExecutor();

        //LOAD MODEL
        try{
            tflite=new Interpreter(loadModelFile());
        }catch (Exception e){
            Log.e("TFLITE","Load error",e);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            startCamera();
        }

        btnTatBaoThuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActive = false;
                stopAlarm();

                if (cameraExecutor != null) {
                    cameraExecutor.shutdown();
                }

                startActivity(new Intent(CameraActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    private Bitmap cropEyeByLandmark(Bitmap bitmap, Face face) {

        FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);
        FaceLandmark rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE);

        if (leftEye == null || rightEye == null) return null;

        float leftX = leftEye.getPosition().x;
        float leftY = leftEye.getPosition().y;

        float rightX = rightEye.getPosition().x;
        float rightY = rightEye.getPosition().y;

        // center mắt
        int centerX = (int)((leftX + rightX) / 2);
        int centerY = (int)((leftY + rightY) / 2);

        // khoảng cách 2 mắt
        int eyeDistance = (int)Math.abs(rightX - leftX);

        // chỉnh size crop
        int width = (int)(eyeDistance * 1.2);
        int height = (int)(eyeDistance * 0.5);

        // dịch lên chút (vì mắt nằm phía trên center)
        int left = Math.max(centerX - width / 2, 0);
        int top = Math.max(centerY - height / 2 - height/4, 0);

        width = Math.min(width, bitmap.getWidth() - left);
        height = Math.min(height, bitmap.getHeight() - top);

        return Bitmap.createBitmap(bitmap, left, top, width, height);
    }

    private MappedByteBuffer loadModelFile() throws IOException{
        AssetFileDescriptor fileDescriptor=getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,
                fileDescriptor.getStartOffset(),
                fileDescriptor.getDeclaredLength());
    }

    //Hàm chạy model 10x20
    private float runModel(Bitmap bitmap){

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 20, 10, true);

        ByteBuffer input = ByteBuffer.allocateDirect(1 * 10 * 20 * 1 * 4);
        input.order(ByteOrder.nativeOrder());

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 20; x++) {
                int pixel = resized.getPixel(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float gray = (r + g + b) / 3.0f / 255.0f;

                input.putFloat(gray);
            }
        }

        float[][] output = new float[1][2];
        tflite.run(input, output);

        return output[0][1];
    }

    //Hàm crop mắt
    private Bitmap cropEye(Bitmap faceBitmap) {
        int w = faceBitmap.getWidth();
        int h = faceBitmap.getHeight();

        return Bitmap.createBitmap(faceBitmap,
                0,
                h / 4,
                w,
                h / 4);
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0); // reset về đầu
        }

        isAlarmPlaying = false;
        eyeClosedCounter = 0; // reset luôn để không kêu lại ngay
        isSaved=false;

        Log.d("ALARM", "STOPPED");
    }

    @ExperimentalGetImage
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }

    @ExperimentalGetImage
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // camera trước
                CameraSelector selector = CameraSelector.DEFAULT_FRONT_CAMERA;

                // giảm tải + tránh crash
                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(240, 320)) // giảm độ phân giải
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build();

                analysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                provider.unbindAll();

                provider.bindToLifecycle(
                        this,
                        selector,
                        preview,
                        analysis
                );

            } catch (Exception e) {
                Log.e("CAMERA", "Start error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @ExperimentalGetImage
    private void analyzeImage(ImageProxy imageProxy) {
        if (!isActive) {
            imageProxy.close();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastTime < 800) {
            imageProxy.close();
            return;
        }
        lastTime = now;

        if (isProcessing) {
            imageProxy.close();
            return;
        }

        isProcessing = true;

        try {
            if (imageProxy.getImage() == null) {
                imageProxy.close();
                isProcessing = false;
                return;
            }

            // CHỈ LẤY 1 LẦN
             Bitmap bitmap = ImageUtils.imageToBitmap(imageProxy);
            //InputImage image = InputImage.fromBitmap(bitmap, 0);
            //int rotation = imageProxy.getImageInfo().getRotationDegrees();
           // InputImage image = InputImage.fromBitmap(bitmap, rotation);
            InputImage image = InputImage.fromMediaImage(
                    imageProxy.getImage(),
                    imageProxy.getImageInfo().getRotationDegrees()
            );


            detector.process(image)
                    .addOnSuccessListener(faces -> {

                        if (!isActive) {
                            imageProxy.close();
                            isProcessing = false;
                            return;
                        }

                        if (!faces.isEmpty()) {
                            Face face = faces.get(0);

                            try {

                                Rect box = face.getBoundingBox();

                                int left = Math.max(box.left, 0);
                                int top = Math.max(box.top, 0);
                                int width = Math.min(box.width(), bitmap.getWidth() - left);
                                int height = Math.min(box.height(), bitmap.getHeight() - top);

//                                Bitmap faceBitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
//                                Bitmap eyeBitmap = cropEye(faceBitmap);

                                FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);

                                if (leftEye != null) {
                                    Log.d("EYE", "LEFT EYE DETECTED");
                                } else {
                                    Log.d("EYE", "LEFT EYE NULL");
                                }
                                Bitmap eyeBitmap = cropEyeByLandmark(bitmap, face);

                                if (eyeBitmap == null) {
                                    imageProxy.close();
                                    isProcessing = false;
                                    return;
                                }

                                float result = runModel(eyeBitmap);

                                Log.d("MODEL", "Close: " + result);

                                if (result > 0.8) {
                                    eyeClosedCounter++;
                                } else if (result < 0.4) {
                                    eyeClosedCounter = Math.max(0, eyeClosedCounter - 1);
                                }

                                if (eyeClosedCounter == 0) {
                                    isSaved = false;
                                }

                                Log.d("COUNTER", "Closed count: " + eyeClosedCounter);

                                if (eyeClosedCounter >= THRESHOLD) {
                                    if (!isAlarmPlaying) {
                                        playAlarm();
                                    }

                                    if (!isSaved) {
                                        saveSleepToServer();
                                        isSaved = true;
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("MODEL", "Error: " + e.getMessage());
                            }
                        }
                    })
                    .addOnCompleteListener(task -> {
                        imageProxy.close();
                        isProcessing = false;
                    });

        } catch (Exception e) {
            imageProxy.close();
            isProcessing = false;
        }
    }

    private void saveSleepToServer() {
        String time=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                .format(new Date());

        //Lấy userId từ login
        SharedPreferences preferences=getSharedPreferences("USER",MODE_PRIVATE);
        String userId=preferences.getString("userId",null);
        String token = preferences.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        if(userId==null){
            Log.e("API","Chưa có mã khách hàng");
            return;
        }

        //Debug
        Log.d("USER_ID", userId);
        Log.d("TOKEN", token);

        // Thêm "Bearer "
        String authHeader = "Bearer " + token;

        SleepRecord record=new SleepRecord(time,eyeClosedCounter,userId);

        ApiService apiService= SleepApiClient.getClient().create(ApiService.class);

        apiService.saveSleep(authHeader,record).enqueue(new Callback<SleepRecord>() {
            @Override
            public void onResponse(Call<SleepRecord> call, Response<SleepRecord> response) {
                Log.d("API","Saved to server");
            }

            @Override
            public void onFailure(Call<SleepRecord> call, Throwable t) {
                Log.e("API","Error: "+t.getMessage());
            }
        });
    }

    private void playAlarm() {
        if (!isAlarmPlaying) {
            mediaPlayer.start();
            isAlarmPlaying = true;
            Log.d("ALARM", "WAKE UP!!!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActive = false; // chặn xử lý tiếp

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (detector != null) detector.close();
        if (cameraExecutor != null) cameraExecutor.shutdownNow();
    }
}
