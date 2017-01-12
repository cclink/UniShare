package cnx.cclink.unishare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;


/**
 * 分享相关的辅助类
 */
public class ShareHelper {

    private static final int THUMB_SIZE_MAX = 150;

    public static Bitmap getThumbBitmapFromFile(String imageFile) {
        Bitmap bmp = BitmapFactory.decodeFile(imageFile);
        float scale = (float) THUMB_SIZE_MAX / Math.max(bmp.getWidth(), bmp.getHeight());
        int thumbWidth = (int) (scale * bmp.getWidth());
        int thumbHeight = (int) (scale * bmp.getHeight());
        return Bitmap.createScaledBitmap(bmp, thumbWidth, thumbHeight, true);
    }

    public static byte[] bmpToByteArray(Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
