package vn.edu.stu.projectnhandientaixenguguc;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;

import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

public class ImageUtils {
    @ExperimentalGetImage
    public static Bitmap imageToBitmap(ImageProxy image) {
        Image mediaImage = image.getImage();
        if (mediaImage == null) return null;

        Image.Plane[] planes = mediaImage.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();

        int width = image.getWidth();
        int height = image.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int yValue = yBuffer.get(y * width + x) & 0xFF;
                int color = 0xFF000000 | (yValue << 16) | (yValue << 8) | yValue;
                bitmap.setPixel(x, y, color);
            }
        }

        return bitmap;
    }
}
