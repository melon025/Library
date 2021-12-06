package info.emm.commonlib.widget.clip;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Z on 2018/7/9.
 */

public class ClipUtil {
    private static final String TAG = ClipUtil.class.getSimpleName();
    private static final String DOT = ".";
    public static final String DCMI_CAMERA_PATH = "/Camera/";
    public static final String SUFFIX_TMP_FILE = "tmp";
    public static final String SUFFIX_AUDIO_FILE = "arm";
    public static final String SUFFIX_VIDEO_FILE = "mp4";
    public static final String SUFFIX_IMAGE_FILE = "png";
    public static final String SUFFIX_IMAGE_THUMBNAIL = "thumbnail";
    public static final int NO_NETWORK = 0;
    public static final int WIFI = 1;
    public static final int NO_WIFI = 2;
    private static ClipUtil.ApplicationEnv mApplicationEnv = new ClipUtil.ApplicationEnv("app");

    public ClipUtil() {
    }

    public static void setupApplicationEnv(ClipUtil.ApplicationEnv applicationEnv) {
        mApplicationEnv = applicationEnv;
    }

    public static ClipUtil.ApplicationEnv getApplicationEnv() {
        return mApplicationEnv;
    }

    public static String getMetaDataValue(Context context, String key) {
        Bundle metaData = null;
        String value = null;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if(null != ai) {
                metaData = ai.metaData;
            }

            if(null != metaData) {
                value = metaData.getString(key);
            }
        } catch (PackageManager.NameNotFoundException var5) {
            ;
        }

        return value;
    }

    public static int getNetWorkType(Context context) {
        if(!isNetWorkAvailable(context)) {
            return 0;
        } else {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
            return cm.getNetworkInfo(1).isConnectedOrConnecting()?1:2;
        }
    }

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
        if(cm == null) {
            return false;
        } else {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isAvailable();
        }
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        Iterator var3 = appProcesses.iterator();

        ActivityManager.RunningAppProcessInfo appProcess;
        do {
            if(!var3.hasNext()) {
                return false;
            }

            appProcess = (ActivityManager.RunningAppProcessInfo)var3.next();
        } while(!appProcess.processName.equals(context.getPackageName()));

        if(appProcess.importance == 400) {
            Log.i("后台", appProcess.processName);
            return true;
        } else {
            Log.i("前台", appProcess.processName);
            return false;
        }
    }

    public static boolean isTopActivity(Context context, String activityName) {
        ActivityManager am = (ActivityManager)context.getSystemService("activity");
        ComponentName cName = am.getRunningTasks(1).size() > 0?((ActivityManager.RunningTaskInfo)am.getRunningTasks(1).get(0)).topActivity:null;
        return null == cName?false:cName.getClassName().equals(activityName);
    }

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static String getDCMIPath() {
        String path = "";
        if(isSDCardMounted()) {
            File sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            path = sdPath.getAbsolutePath() + "/Camera/";
            File f = new File(path);
            boolean flag = false;
            if(!f.exists()) {
                flag = f.mkdirs();
            }
        }

        return path;
    }

    public static void clearCache(Context ctx) {
        File cacheDir = getAppCacheDir(ctx);
        if(cacheDir != null && cacheDir.exists() && cacheDir.isDirectory()) {
            File[] var2 = cacheDir.listFiles();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                File item = var2[var4];
                item.delete();
            }
        }

    }

    public static long calcCacheSize(Context ctx) {
        File cacheDir = getAppCacheDir(ctx);
        long total = 0L;
        if(cacheDir != null) {
            try {
                total = (new ClipUtil.ConcurrentTotalFileSizeWLatch()).getTotalSizeOfFile(cacheDir.getAbsolutePath());
            } catch (InterruptedException var5) {
                Log.e(TAG, var5.getMessage(), var5);
            }
        }

        return total;
    }

    public static File getAppCacheFile(String fileName, Context ctx) throws Exception {
        File cacheDir = getAppCacheDir(ctx);
        return cacheDir != null && cacheDir.exists()?new File(cacheDir, fileName):null;
    }

    public static File getAppCacheDir(Context ctx) {
        boolean mounted = isSDCardMounted();
        File cacheFile = null;
        if(!mounted) {
            Log.w(TAG, "SD card did not mounted");
            mounted = false;
            cacheFile = ctx.getCacheDir();
        }

        if(mounted) {
            File storageRoot = Environment.getExternalStorageDirectory();
            cacheFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat(mApplicationEnv.getAppCacheDir()));
            if(!cacheFile.exists()) {
                boolean result = cacheFile.mkdirs();
                if(result) {
                    return cacheFile;
                }

                return null;
            }
        }

        return cacheFile;
    }

    public static File getAppTmpFile(String fileName) throws Exception {
        if(!isSDCardMounted()) {
            Log.e(TAG, "SD card did not mounted");
            throw new Exception("SD card did not mounted");
        } else {
            File storageRoot = Environment.getExternalStorageDirectory();
            File tmpFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat(mApplicationEnv.getAppTmpDir()));
            if(!tmpFile.exists()) {
                boolean result = tmpFile.mkdirs();
                return result?new File(tmpFile, fileName):null;
            } else {
                return new File(tmpFile, fileName);
            }
        }
    }

    public static File getAppThumbnailCacheFile(String fileName) throws Exception {
        if(!isSDCardMounted()) {
            Log.e(TAG, "SD card did not mounted");
            throw new Exception("SD card did not mounted");
        } else {
            File storageRoot = Environment.getExternalStorageDirectory();
            File tmpFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat(mApplicationEnv.getAppThumbnailDir()));
            if(!tmpFile.exists()) {
                boolean result = tmpFile.mkdirs();
                return result?new File(tmpFile, fileName):null;
            } else {
                return new File(tmpFile, fileName);
            }
        }
    }

    public static File getLogFile(String fileName) {
        if(!isSDCardMounted()) {
            Log.e(TAG, "SD card did not mounted");
            return null;
        } else {
            File storageRoot = Environment.getExternalStorageDirectory();
            File tmpFile = new File(storageRoot.getAbsolutePath().concat(File.separator).concat(mApplicationEnv.getAppLogDir()));
            if(!tmpFile.exists()) {
                boolean result = tmpFile.mkdirs();
                return result?new File(tmpFile, fileName):null;
            } else {
                return new File(tmpFile, fileName);
            }
        }
    }

    public static boolean hasPermission() {
        return true;
    }

    public static int dip2px(float density, float dpValue) {
        return (int)(dpValue * density + 0.5F);
    }

    public static int px2dip(float density, float pxValue) {
        return (int)(pxValue / density + 0.5F);
    }

    public static int dip2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * density + 0.5F);
    }

    public static int px2dip(Context context, float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / density + 0.5F);
    }

    public static String generateFileName(ClipUtil.FileType type) {
        String fileName = UUID.randomUUID().toString();
        switch(type) {
            case FILE_TYPE_IMAGE:
                fileName = fileName.concat(".").concat("png");
                break;
            case FILE_TYPE_AUDIO:
                fileName = fileName.concat(".").concat("arm");
                break;
            case FILE_TYPE_VIDEO:
                fileName = fileName.concat(".").concat("mp4");
                break;
            case FILE_TYPE_TMP:
                fileName = fileName.concat(".").concat("tmp");
                break;
            case FILE_TYPE_THUMBNAIL:
                fileName = fileName.concat(".").concat("thumbnail");
        }

        return fileName;
    }

    public static long getSDFreeSize() {
        if(isSDCardMounted()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(path.getPath());
            long blockSize = (long)sf.getBlockSize();
            long freeBlocks = (long)sf.getAvailableBlocks();
            return freeBlocks * blockSize / 1024L / 1024L;
        } else {
            return -1L;
        }
    }

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];

        for(int i = 0; i < 4; ++i) {
            b[i] = (byte)(n >> 24 - i * 8);
        }

        return b;
    }

    public static byte[] float2byte(float f) {
        int fbit = Float.floatToIntBits(f);
        byte[] b = new byte[4];

        int len;
        for(len = 0; len < 4; ++len) {
            b[len] = (byte)(fbit >> 24 - len * 8);
        }

        len = b.length;
        byte[] dest = new byte[len];
        System.arraycopy(b, 0, dest, 0, len);

        for(int i = 0; i < len / 2; ++i) {
            byte temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;
    }

    public static int byteArray2int(byte[] b) {
        return (b[0] << 24) + (b[1] << 16) + (b[2] << 8) + b[3];
    }

    private static String matchUrl(String text) {
        if(TextUtils.isEmpty(text)) {
            return null;
        } else {
            Pattern p = Pattern.compile("[a-zA-z]+://[^\\s]*", 2);
            Matcher matcher = p.matcher(text);
            return matcher.find()?matcher.group():null;
        }
    }

    private static String getMatchUrl(String text, String cmpUrl) {
        String url = matchUrl(text);
        return url != null && url.contains(cmpUrl)?url:null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{"_data"};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, (String)null);
            if(cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow("_data");
                String var8 = cursor.getString(index);
                return var8;
            }
        } finally {
            if(cursor != null) {
                cursor.close();
            }

        }

        return null;
    }

    public static void hideInputKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if(view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService("input_method");
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public static String getAppVersionName(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException var2) {
            return "未知版本";
        }
    }

    public static int getAppVersionCode(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException var2) {
            return -1;
        }
    }

    public static String getUUID(Context ctx) {
        TelephonyManager tManager = (TelephonyManager)ctx.getSystemService("phone");
        return tManager.getDeviceId();
    }

    public static int getAndroidSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getRealFilePath(Context context, Uri uri) {
        if(null == uri) {
            return null;
        } else {
            String scheme = uri.getScheme();
            String data = null;
            if(scheme == null) {
                data = uri.getPath();
            } else if("file".equals(scheme)) {
                data = uri.getPath();
            } else if("content".equals(scheme)) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, (String)null, (String[])null, (String)null);
                if(null != cursor) {
                    if(cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex("_data");
                        if(index > -1) {
                            data = cursor.getString(index);
                        }
                    }

                    cursor.close();
                }
            }

            return data;
        }
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static class ApplicationEnv {
        private String appBaseDir;
        private String appCacheDir;
        private String appThumbnailDir;
        private String appTmpDir;
        private String appLogDir;
        private String appSoundDir;
        private String appVideoDir;
        private String appImageDir;

        public ApplicationEnv(String appBaseDir) {
            this.appBaseDir = appBaseDir;
            this.appCacheDir = appBaseDir + File.separator + "cache";
            this.appThumbnailDir = appBaseDir + File.separator + "thumbnail";
            this.appTmpDir = appBaseDir + File.separator + "tmp";
            this.appLogDir = appBaseDir + File.separator + "log";
            this.appSoundDir = appBaseDir + File.separator + "sound";
            this.appVideoDir = appBaseDir + File.separator + "video";
            this.appImageDir = appBaseDir + File.separator + "image";
        }

        public String getAppBaseDir() {
            return this.appBaseDir;
        }

        public String getAppCacheDir() {
            return this.appCacheDir;
        }

        public String getAppThumbnailDir() {
            return this.appThumbnailDir;
        }

        public String getAppTmpDir() {
            return this.appTmpDir;
        }

        public String getAppLogDir() {
            return this.appLogDir;
        }

        public String getAppSoundDir() {
            return this.appSoundDir;
        }

        public String getAppVideoDir() {
            return this.appVideoDir;
        }

        public String getAppImageDir() {
            return this.appImageDir;
        }
    }

    public static enum FileType {
        FILE_TYPE_AUDIO,
        FILE_TYPE_VIDEO,
        FILE_TYPE_IMAGE,
        FILE_TYPE_TMP,
        FILE_TYPE_THUMBNAIL;

        private FileType() {
        }
    }

    public static class ConcurrentTotalFileSizeWLatch {
        private ExecutorService service;
        private final AtomicLong pendingFileVisits = new AtomicLong();
        private final AtomicLong totalSize = new AtomicLong();
        private final CountDownLatch latch = new CountDownLatch(1);

        public ConcurrentTotalFileSizeWLatch() {
        }

        private void updateTotalSizeOfFilesInDir(File file) {
            long fileSize = 0L;
            if(file.isFile()) {
                fileSize = file.length();
            } else {
                File[] children = file.listFiles();
                if(children != null) {
                    File[] var5 = children;
                    int var6 = children.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        final File child = var5[var7];
                        if(child.isFile()) {
                            fileSize += child.length();
                        } else {
                            this.pendingFileVisits.incrementAndGet();
                            this.service.execute(new Runnable() {
                                public void run() {
                                    ConcurrentTotalFileSizeWLatch.this.updateTotalSizeOfFilesInDir(child);
                                }
                            });
                        }
                    }
                }
            }

            this.totalSize.addAndGet(fileSize);
            if(this.pendingFileVisits.decrementAndGet() == 0L) {
                this.latch.countDown();
            }

        }

        public long getTotalSizeOfFile(String fileName) throws InterruptedException {
            this.service = Executors.newFixedThreadPool(100);
            this.pendingFileVisits.incrementAndGet();

            long var2;
            try {
                this.updateTotalSizeOfFilesInDir(new File(fileName));
                this.latch.await(100L, TimeUnit.SECONDS);
                var2 = this.totalSize.longValue();
            } finally {
                this.service.shutdown();
            }

            return var2;
        }
    }
}
