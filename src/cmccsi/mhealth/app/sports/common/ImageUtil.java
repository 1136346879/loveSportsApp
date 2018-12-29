package cmccsi.mhealth.app.sports.common;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;
import cmccsi.mhealth.app.sports.R;

public class ImageUtil {
    protected static final String TAG = "AsyncBitmapLoader";
    private static final String mImgUrl = Environment
            .getExternalStorageDirectory() + "/ishang_image/";// +MD5.getMD5(url));
    /**
     * 内存图片软引用缓冲
     */
    private final HashMap<String, SoftReference<Bitmap>> mImageCache = new HashMap<String, SoftReference<Bitmap>>();

    private static final int HARD_CACHE_CAPACITY = 10;
    BitmapDrawable bd;

    // private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in
    // milliseconds

    public HashMap<String, Bitmap> getSHardBitmapCache() {
        return sHardBitmapCache;
    }

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
            HARD_CACHE_CAPACITY, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(
                LinkedHashMap.Entry<String, Bitmap> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                // Entries push-out of hard reference cache are transferred to
                // soft reference cache
                // eldest.getKey() 为集合顺序最前面的地址
                sSoftBitmapCache.put(eldest.getKey(),
                        new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };
    static ImageUtil mAsyncBitmapLoader = null;

    public static synchronized ImageUtil getInstance() {
        if (mAsyncBitmapLoader == null) {
            mAsyncBitmapLoader = new ImageUtil();
        }
        return mAsyncBitmapLoader;
    }

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
            HARD_CACHE_CAPACITY / 2);

    public Drawable loadBitmap(final ImageView imageView, String imageURL,
            String tag, int mode) {
        if (imageURL == null || imageView == null)
            return null;
        String[] modeurl = new String[] { imageURL, "" };
        // 大图模式！
        String imageURLBIG = "";
        if (mode == 1) {
            imageURLBIG = imageURL.substring(0, imageURL.lastIndexOf("."))
                    + "_big.jpg";
            modeurl[1] = imageURLBIG;
        }
        synchronized (sHardBitmapCache) {
            String md5Str = Encrypt.getMD5Str(getSubFileName(modeurl[mode]));
            Bitmap bitmap = sHardBitmapCache.get(md5Str);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(imageURL);
                sHardBitmapCache.put(imageURL, bitmap);

                imageView.setImageBitmap(bitmap);

                return imageView.getDrawable();
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(Encrypt
                .getMD5Str(getSubFileName(modeurl[mode])));
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                imageView.setImageBitmap(bitmap);
                return imageView.getDrawable();
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(imageURL);
            }
        }
        /**
         * 加上一个对本地缓存的查找
         */
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // String bitmapName = imageURL.substring(imageURL.lastIndexOf("/")
            // + 1);
            int nEnd;
            String bitmapName = imageURL;
            if ((imageURL.lastIndexOf("/") + 1) < imageURL.lastIndexOf(".")) {

                if ((nEnd = imageURL.lastIndexOf(".")) != -1) {
                    bitmapName = imageURL.substring(
                            imageURL.lastIndexOf("/") + 1, nEnd);
                } else {
                    bitmapName = imageURL
                            .substring(imageURL.lastIndexOf("/") + 1);
                }
            }
            File cacheDir = new File(mImgUrl);
            File[] cacheFiles = cacheDir.listFiles();
            if (cacheFiles != null) {
                for (int i = 0; i < cacheFiles.length; i++) {
                    try {
                        // 字符串名加密匹配本地文件名
                        // String name = Encrypt.encryptBASE64(bitmapName);
                        String name = Encrypt.getMD5Str(bitmapName);
                        if (name.equals(cacheFiles[i].getName())) {
                            long fileSizes = Common.getFileSizes(cacheFiles[i]);
                            Logger.i(TAG, "fileSizes==" + fileSizes
                                    + "//bitmapName==" + bitmapName);

                            // 大图模式时判断拿到的图是否是大图，如果是小图则break去下载大图！
                            if (mode == 1)
                                if (fileSizes < 5012)
                                    break;

                            // 流读取,比BitmapFactory.decodeFile效率好并且便于加密
                            FileInputStream is = new FileInputStream(mImgUrl
                                    + name);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            if (bitmap == null) {
                                // throw new
                                // NullPointerException(TAG+"bitmap is null,BitmapFactory decodeFile erroe! "
                                // + mImgUrl + bitmapName);//异常信息
                                File errorfile = new File(mImgUrl + name);
                                boolean delete = errorfile.delete();
                                Logger.e(
                                        imageURL,
                                        (TAG
                                                + "bitmap is null,BitmapFactory decodeFile erroe! "
                                                + mImgUrl + bitmapName
                                                + "//delete success?==>" + delete));// 异常信息)
                                return null;
                            }
                            imageView.setImageBitmap(bitmap);
                            if (!(sHardBitmapCache.containsKey(name)))
                                sHardBitmapCache.put(name, bitmap);
                            return null;
                        }
                    } catch (FileNotFoundException e) {
                        Logger.e(TAG, e.getMessage() + (mImgUrl + bitmapName));
                    } catch (Exception e) {
                        // Logger.e(TAG, e.getMessage());
                    }
                }
            }
        }
        if (imageURL.startsWith("http")) {
            if (cancelPotentialDownload(imageURL, imageView)) {
                BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                task.execute(modeurl[mode], tag, mode + "");
            }
        }
        return null;
    }

    public Drawable loadBitmap(final ImageView imageView,
            final String imageURL, String tag) {
        return loadBitmap(imageView, imageURL, tag, 0);
    }

    public Drawable loadBitmap(final ImageView imageView, final String imageURL) {
        return loadBitmap(imageView, imageURL, null);
    }

    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private String tag;
        private int mode;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            tag = params[1];
            mode = Integer.parseInt(params[2]);
            return downloadBitmap(url, mode);
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                // Change bitmap only if this process is still associated with
                // it
                // Or if we don't use any bitmap to task association
                // (NO_DOWNLOADED_DRAWABLE mode)
                if (imageView != null && bitmap != null) {
                    if (tag != null) {
                        if (!tag.equals(imageView.getTag().toString())) {
                            return;
                        }
                    }
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    private static boolean cancelPotentialDownload(String url,
            ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static BitmapDownloaderTask getBitmapDownloaderTask(
            ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.BLACK);
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
                    bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    private Bitmap downloadBitmap(String url2, int mode) {
        try {
            // String name = url2.substring(url2.lastIndexOf("/") + 1,
            // url2.lastIndexOf("."));
            // url2 = url2.substring(0,url2.lastIndexOf("/") +
            // 1)+BASE64.encryptBASE64(name)+".jpg";
            long start = System.currentTimeMillis();
            URL m = new URL(url2);
            InputStream bitmapIs = (InputStream) m.getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(bitmapIs);
            // if(bitmap ==null) return null;
            long end = System.currentTimeMillis();
            System.out.println(" time = " + (end - start));

            // ↓这句话用于将大头像与小头像同名
            if (mode == 1) {
                if (url2.contains("_big")) {
                    url2 = url2.split("_big")[0] + ".jpg";
                }
            }

            mImageCache.put(url2, new SoftReference<Bitmap>(bitmap));
            // Message msg = handler.obtainMessage(0, bitmap);
            // handler.sendMessage(msg);

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File dir = new File(mImgUrl);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String MDName = url2;
                if (url2.lastIndexOf("/") + 1 < url2.lastIndexOf(".")) {
                    MDName = Encrypt.getMD5Str(url2.substring(
                            url2.lastIndexOf("/") + 1, url2.lastIndexOf(".")));
                }
                File bitmapFile = new File(mImgUrl + MDName);
                if (null != bitmapFile && !bitmapFile.exists()) {
                    try {
                        bitmapFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(bitmapFile);
                    // 30 是压缩率，表示压缩70%; 如果不压缩是100，表示压缩率为0
                    if (bitmap == null)
                        return bitmap;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return bitmap;
        } catch (IOException e) {
            // Logger.e(TAG, e.printStackTrace());
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 图片的缩放方法
     * 
     * @param bgimage
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
            double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 从本地文件中删除文件
     * 
     * @param imagePath
     */
    @SuppressWarnings("unused")
    private static void deleteImageFromLocal(String imagePath) {
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 
     * getSubFileName(获取文件名)
     * 
     * @创建人：Qiujunjie - 邱俊杰
     * @创建时间：2013-9-18 下午6:42:09
     * @修改人：Qiujunjie - 邱俊杰
     * @修改时间：2013-9-18 下午6:42:09
     */
    public String getSubFileName(String url) {
        String filename = url;
        int end;
        if ((url.lastIndexOf("/") + 1) < url.lastIndexOf(".")) {
            if ((end = url.lastIndexOf(".")) != -1) {
                filename = url.substring(url.lastIndexOf("/") + 1, end);
            } else {
                filename = url.substring(url.lastIndexOf("/") + 1);
            }
        }
        return filename;
    }

    /**
     * Bitmap2Bytes(图片传成字节数组)
     * 
     * @param 图片
     * @return byte[]
     * @Exception 异常对象
     * @创建人：Qiujunjie - 邱俊杰
     * @创建时间：2013-10-9 下午3:44:32
     * @修改人：Qiujunjie - 邱俊杰
     * @修改时间：2013-10-9 下午3:44:32
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        // int fgWidth = foreground.getWidth();
        // int fgHeight = foreground.getHeight();
        // create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth,
                bgHeight + foreground.getHeight(),
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        // draw fg into

        cv.drawBitmap(foreground, 0, 0, null);// 在 0，0坐标开始画入fg ，可以从任意位置画入
        cv.drawBitmap(background, 0, foreground.getHeight(), null);// 在
                                                                   // 0，0坐标开始画入bg
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return newbmp;
    }

    public static Bitmap toConformBitmapWithNewType(Context context,
            Bitmap background, Bitmap foreground, String datetime) {
        if (background != null && foreground != null) {
            if (background.getWidth() > foreground.getWidth()) {
                int width = foreground.getWidth();
                int height = foreground.getHeight();
                Matrix matrix = new Matrix();
                float scaleW = background.getWidth() / (float) width;
                float scaleH = scaleW;
                matrix.postScale(scaleW, scaleH);
                foreground = Bitmap.createBitmap(foreground, 0, 0, width, height, matrix, true);
            } else if (background.getWidth() < foreground.getWidth()) {
                int width = background.getWidth();
                int height = background.getHeight();
                Matrix matrix = new Matrix();
                float scaleW = foreground.getWidth() / (float) width;
                float scaleH = scaleW;
                matrix.postScale(scaleW, scaleH);
                background = Bitmap.createBitmap(background, 0, 0, width, height, matrix, true);
            }
        }
        int tempheight = 0;
        tempheight = foreground == null ? tempheight : tempheight
                + foreground.getHeight();
        tempheight = background == null ? tempheight : tempheight
                + background.getHeight();
        if (tempheight == 0)
            return null;
        if (background == null)
            background = foreground;
        int bgWidth = background.getWidth();
        //int bgHeight = background.getHeight();
        // int fgWidth = foreground.getWidth();
        // int fgHeight = foreground.getHeight();
        // create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap title = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cutetitle);
        title = scaleBitmapWithSpecialWidth(bgWidth, title);
        Bitmap bottom = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cutebottom);
        bottom = scaleBitmapWithSpecialWidth(bgWidth, bottom);

        Bitmap newbmp = Bitmap.createBitmap(bgWidth,
                tempheight + title.getHeight(),
                android.graphics.Bitmap.Config.ARGB_8888);
        int picw = newbmp.getWidth();
        int pich = newbmp.getHeight();
        int[] pix = new int[picw * pich];
        for (int y = 0; y < pich; y++)
            for (int x = 0; x < picw; x++) {
                int index = y * picw + x;
                int r = ((pix[index] >> 16) & 0xff) | 0xff;
                int g = ((pix[index] >> 8) & 0xff) | 0xff;
                int b = (pix[index] & 0xff) | 0xff;
                pix[index] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        newbmp.setPixels(pix, 0, picw, 0, 0, picw, pich);

        Canvas cv = new Canvas(newbmp);
        // draw fg into
        Paint p = new Paint();
        Paint ptext = new Paint();
        p.setAlpha(200);
        ptext.setARGB(255, 255, 255, 255);
        ptext.setTextSize(40);
        
        cv.drawBitmap(bottom, 0,
                tempheight - bottom.getHeight() + title.getHeight(), null);
        cv.drawBitmap(title, 0, 0, null);
        if(datetime!=null)
        {
	        cv.drawText(datetime, background.getWidth() - ptext.measureText(datetime)-30,
	                title.getHeight() / 2 , ptext);
        }
        if (foreground != null) {
            cv.drawBitmap(foreground, 0, title.getHeight(), p);// 在 0，0坐标开始画入fg
                                                               // ，可以从任意位置画入
        }
        cv.drawBitmap(background, 0, tempheight - background.getHeight()
                + title.getHeight(), p);// 在 0，0坐标开始画入bg
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        title.recycle();
        bottom.recycle();
        return newbmp;
    }

    /**
     * 使图片的宽度保持一致，取图片中宽度较小者的宽度作为最终宽度
     * 
     * @param background
     * @param foreground
     */
    private static void formatSrcBitmap(Bitmap bitmap1, Bitmap bitmap2) {
        if (bitmap1 != null && bitmap2 != null) {
            if (bitmap1.getWidth() > bitmap2.getWidth()) {
                int width = bitmap1.getWidth();
                int height = bitmap1.getHeight();
                Matrix matrix = new Matrix();
                float scaleW = bitmap2.getWidth() / (float) width;
                float scaleH = scaleW;
                matrix.postScale(scaleW, scaleH);
                bitmap1 = Bitmap.createBitmap(bitmap2, 0, 0, width, height, matrix, true);
            } else if (bitmap1.getWidth() < bitmap2.getWidth()) {
                int width = bitmap2.getWidth();
                int height = bitmap2.getHeight();
                Matrix matrix = new Matrix();
                float scaleW = bitmap1.getWidth() / (float) width;
                float scaleH = scaleW;
                matrix.postScale(scaleW, scaleH);
                bitmap2 = Bitmap.createBitmap(bitmap2, 0, 0, width, height, matrix, true);
            }
        }
    }

    /**
     * 以特定的宽度，缩放图片
     * @param bgWidth
     * @param bitmap
     * @return
     */
    private static Bitmap scaleBitmapWithSpecialWidth(int bgWidth, Bitmap bitmap) {
        int finalWidth = bgWidth;
        int finalHeight=(int)(1.0*bgWidth/bitmap.getWidth()*bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, false);
    }
    
    /**
     * 
     * @param image 图片
     * @param size 大小 kb
     * @return
     */
    public static Bitmap compressImage(Bitmap image,int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        double oldsize=baos.toByteArray().length/1024;
//        int Multiple=(int)Math.ceil((size/oldsize)*100);
//        baos.reset();//重置baos即清空baos
//        image.compress(Bitmap.CompressFormat.JPEG, Multiple, baos);//这里压缩options%，把压缩后的数据存放到baos中
//        Logger.d("cjz", "压缩比例"+ Multiple);
//        Logger.d("cjz", "压缩图片前"+oldsize);
        int options = 100;
        while ( baos.toByteArray().length / 1024>size) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩       
            baos.reset();//重置baos即清空baos
            options -= 15;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        Logger.d("cjz", "压缩图片后"+baos.toByteArray().length/1024);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    
    /** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public void saveFile(Bitmap bm,String path, String fileName) throws IOException {  
        File dirFile = new File(path);  
        if(!dirFile.exists()){  
            dirFile.mkdir();  
        }  
        File myCaptureFile = new File(path + fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
        bos.flush();  
        bos.close();  
    }
}
