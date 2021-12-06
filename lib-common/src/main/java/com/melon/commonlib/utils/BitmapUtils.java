package info.emm.commonlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.util.Log;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import info.emm.commonlib.widget.clip.ClipUtil;

import static info.emm.commonlib.widget.clip.ClipUtil.FileType.FILE_TYPE_IMAGE;

/**
 * Created by Z on 2018/7/9.
 */

public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    public BitmapUtils() {
    }

    public static Bitmap rotateBitmap(int angle, Bitmap source) {
        Bitmap target = null;
        Matrix matrix = new Matrix();
        matrix.postRotate((float)angle);
        target = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return target;
    }

    public static int readBitmapDegree(String path) {
        short degree = 0;

        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt("Orientation", 1);
            switch(orientation) {
                case 3:
                    degree = 180;
                    break;
                case 6:
                    degree = 90;
                    break;
                case 8:
                    degree = 270;
            }
        } catch (IOException var4) {
            Log.e(TAG, var4.getMessage(), var4);
        }

        return degree;
    }

    public static String readBitmapSize(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int w = opts.outWidth;
        int h = opts.outHeight;
        StringBuilder sb = new StringBuilder();
        sb.append(w).append("*").append(h);
        return sb.toString();
    }

    private static BitmapFactory.Options bitmapManage(int inSampleSize) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inSampleSize = inSampleSize;
        return opts;
    }

    public static String saveBitmapByUri(Context ctx, int degree, String sPath, float ww, float hh) throws Exception {
        Bitmap bitmap = compressBitmap(sPath, ww, hh);
        bitmap = rotateBitmap(degree, bitmap);
        String photoName = ClipUtil.generateFileName(FILE_TYPE_IMAGE);
        File imageFile = ClipUtil.getAppCacheFile(photoName, ctx);
        if(imageFile == null) {
            throw new RuntimeException("photo cannot be created.");
        } else {
            writeToSdcard(bitmap, imageFile);
            return imageFile.getAbsolutePath();
        }
    }

    public static void saveBitmap(Bitmap src, String outFile, float ww, float hh) throws Exception {
        Bitmap newBitmap = compressBitmap(src, ww, hh);
        writeToSdcard(newBitmap, new File(outFile));
    }

    public static void saveBitmap(String src, String outFile, float ww, float hh) {
        Bitmap newBitmap = compressBitmap(src, ww, hh);
        writeToSdcard(newBitmap, new File(outFile));
    }

    public static Bitmap compressBitmap(Bitmap src, float ww, float hh) throws Exception {
        return compBitmapByScale(src, ww, hh);
    }

    public static Bitmap compressBitmap(String path, float ww, float hh) {
        if(ww <= 0.0F || hh <= 0.0F) {
            ww = 480.0F;
            hh = 800.0F;
        }

        return compBitmapByScale(path, ww, hh);
    }

    public static Bitmap compBitmapByScale(String path, float ww, float hh) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int w = opts.outWidth;
        int h = opts.outHeight;
        int be = 1;
        if(w >= h && (float)w > ww) {
            be = Math.round((float)opts.outWidth / ww);
        } else if(w <= h && (float)h > hh) {
            be = Math.round((float)opts.outHeight / hh);
        }

        if(be <= 0) {
            be = 1;
        }

        return BitmapFactory.decodeFile(path, bitmapManage(be));
    }

    public static Bitmap compBitmapByScale(Bitmap source, float ww, float hh) throws Exception {
        if(source == null) {
            throw new IllegalArgumentException("source can not be empty.");
        } else {
            if(ww <= 0.0F || hh <= 0.0F) {
                ww = 480.0F;
                hh = 800.0F;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            source.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(isBm, (Rect)null, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            int be = 1;
            if(w >= h && (float)w > ww) {
                be = Math.round((float)newOpts.outWidth / ww);
            } else if(w <= h && (float)h > hh) {
                be = Math.round((float)newOpts.outHeight / hh);
            }

            if(be <= 0) {
                be = 1;
            }

            newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            newOpts.inSampleSize = be;
            isBm = new ByteArrayInputStream(baos.toByteArray());
            Bitmap newBitmap = BitmapFactory.decodeStream(isBm, (Rect)null, newOpts);
            closeInputStream(isBm);
            closeOutputStream(baos);
            return newBitmap;
        }
    }

    private static void closeInputStream(InputStream in) {
        if(null != in) {
            try {
                in.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    private static void closeOutputStream(OutputStream out) {
        if(null != out) {
            try {
                out.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static ByteArrayOutputStream compBitmap(Bitmap src, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        if(baos.toByteArray().length / 1024 > size) {
            while(baos.toByteArray().length / 1024 > size) {
                baos.reset();
                src.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
        }

        return baos;
    }

    public static Bitmap compBitmapByQuality(Bitmap src, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        if(baos.toByteArray().length / 1024 > size) {
            while(baos.toByteArray().length / 1024 > size) {
                baos.reset();
                src.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, (Rect)null, (BitmapFactory.Options)null);
    }

    public static void writeToSdcard(Bitmap bitmap, File outFile) {
        if(!ClipUtil.isSDCardMounted()) {
            throw new RuntimeException("sdcard is not available.");
        } else {
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(outFile);
                if(bitmap != null && bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                    fileOutputStream.flush();
                }
            } catch (IOException var7) {
                outFile.delete();
                Log.e(TAG, var7.getMessage(), var7);
            } finally {
                if(null != bitmap) {
                    bitmap.recycle();
                }

            }

        }
    }
}
