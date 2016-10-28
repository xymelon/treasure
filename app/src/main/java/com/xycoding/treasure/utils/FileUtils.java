package com.xycoding.treasure.utils;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by xuyang on 15/11/18.
 */
public class FileUtils {

    public static final String DIR_TMP = "tmp";

    /**
     * 保存内容到指定目录
     *
     * @param bytes
     * @param dir
     * @param fileName
     * @return
     */
    public static boolean saveFile(byte[] bytes, File dir, String fileName) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return saveFile(bytes, new File(dir, fileName));
    }

    /**
     * 保存内容到指定文件
     *
     * @param bytes
     * @param file
     * @return
     */
    public static boolean saveFile(byte[] bytes, File file) {
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean saveBitmap2File(Bitmap bitmap, File file) {
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除文件（若为目录，则递归删除子目录和文件）
     *
     * @param file
     */
    public static void delFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            // 删除子目录和文件
            for (File subFile : file.listFiles()) {
                delFile(subFile);
            }
        } else {
            file.delete();
        }
    }

    public static long getDatabaseSize(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        if (dbFile != null) {
            return dbFile.length();
        }
        return 0;
    }

    public static long getFileSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                size += getFileSize(subFile);
            }
        } else {
            size = file.length();
        }
        return size;
    }

    public static boolean isExists(String path) {
        return new File(path).exists();
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context    The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalCacheDir();
            // In some case, even the sd card is mounted,
            // getExternalCacheDir will return null
            // may be it is nearly full.
            if (path != null) {
                return path;
            }
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static Uri fileUri2ContentUri(final Context context, final Uri uri) {
        // file uri to content uri
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return getContentUri(context, new File(uri.getPath()));
        }
        return uri;
    }

    private static Uri getContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
