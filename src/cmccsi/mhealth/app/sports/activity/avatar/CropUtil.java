package cmccsi.mhealth.app.sports.activity.avatar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

/**
 * 压缩图片的工具
 * 
 */
public class CropUtil {

    /**
     * 关闭IO流
     * 
     * @param in
     * @param out
     */
    public static void closeIO(InputStream in, OutputStream out) {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缓存图片到本地存储卡
     * 
     * @param context
     *            上线文
     * @param uri
     *            图片对应点的uri
     * @param cacheFullPath
     *            缓存全路径
     * @param isRoate
     *            是否翻转
     * @return 本地文件
     */
    @SuppressWarnings("deprecation")
	public static File makeTempFile(Context context, Uri uri, String cacheFullPath, int nRoate) {
        Bitmap photo = null;
        int dw = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        int dh = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        // 已屏幕宽 和一半的高作为图片显示的最大尺寸
        try {
            BitmapFactory.Options factory = new BitmapFactory.Options();
            factory.inJustDecodeBounds = true; // 当为true时 允许查询图片不为 图片像素分配内存
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, factory);
            int hRatio = (int) Math.ceil(factory.outHeight / (float) dh); // 图片是高度的几倍
            int wRatio = (int) Math.ceil(factory.outWidth / (float) dw); // 图片是宽度的几倍
            // 缩小到 1/ratio的尺寸和 1/ratio^2的像素
            if (hRatio > 1 || wRatio > 1) {
                if (hRatio > wRatio) {
                    factory.inSampleSize = hRatio;
                } else
                    factory.inSampleSize = wRatio;
            }
            factory.inJustDecodeBounds = false;
            photo = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, factory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (photo != null) {
            File bFile = new File(cacheFullPath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(bFile);
                byte[] bmpBytes = compressPhotoByte(photo, nRoate);
                fos.write(bmpBytes);
                fos.flush();
                if (bFile.exists() && bFile.length() > 0)
                    return bFile;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CropUtil.closeIO(null, fos);
            }
        }
        return null;
    }

    /**
     * 压缩图片并返回图片字节数据
     * 
     * @param b
     *            ：被压缩的图片
     * @param size
     *            :指定被压缩后的最大容量
     * @param isRoate
     *            :是否翻转
     * @return
     */
    public static byte[] compressPhotoByte(Bitmap sBmp, int nRoate) {
        int w = sBmp.getWidth();
        int h = sBmp.getHeight();

        Matrix matrix = new Matrix();
//        if (isRoate) {
//            if (w > h) {
//                matrix.postRotate(90);
//            }
//        }
                matrix.postRotate(nRoate );
        // 压缩图片
        Bitmap newB = Bitmap.createBitmap(sBmp, 0, 0, w, h, matrix, false);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        newB.compress(CompressFormat.JPEG, 100, bos);
        sBmp.recycle();
        return bos.toByteArray();
    }
}
