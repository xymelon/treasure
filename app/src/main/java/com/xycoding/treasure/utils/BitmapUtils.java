package com.xycoding.treasure.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.util.Pair;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class BitmapUtils {
    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;

    public static int getExifOrientation(Context context, Uri uri) {
        String authority = uri.getAuthority().toLowerCase();
        int orientation;
        if (authority.endsWith("media")) {
            orientation = getExifRotation(context, uri);
        } else {
            orientation = getExifRotation(new File(getRealPathFromURI(context, uri)));
        }
        return orientation;
    }

    public static int getExifRotation(File file) {
        if (file == null) return 0;
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            return getRotateDegreeFromOrientation(
                    exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getExifRotation(Context context, Uri uri) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return 0;
            }
            return cursor.getInt(0);
        } catch (RuntimeException ignored) {
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int getRotateDegreeFromOrientation(int orientation) {
        int degree = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                break;
        }
        return degree;
    }

    /**
     * Gets the real path from file
     *
     * @param context
     * @param contentUri
     * @return path
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return getPathForV19AndUp(context, contentUri);
        } else {
            return getPathForPreV19(context, contentUri);
        }
    }

    /**
     * Handles pre V19 uri's
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getPathForPreV19(Context context, Uri contentUri) {
        String res = null;

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /**
     * Handles V19 and up uri's
     *
     * @param context
     * @param contentUri
     * @return path
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathForV19AndUp(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    public static Matrix getMatrixFromExifOrientation(int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1.0f, 1.0f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180.0f);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1.0f, -1.0f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90.0f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.postRotate(-90.0f);
                matrix.postScale(1.0f, -1.0f);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.postRotate(90.0f);
                matrix.postScale(1.0f, -1.0f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(-90.0f);
                break;
        }
        return matrix;
    }

    public static int getExifOrientationFromAngle(int angle) {
        int normalizedAngle = angle % 360;
        switch (normalizedAngle) {
            case 0:
                return ExifInterface.ORIENTATION_NORMAL;
            case 90:
                return ExifInterface.ORIENTATION_ROTATE_90;
            case 180:
                return ExifInterface.ORIENTATION_ROTATE_180;
            case 270:
                return ExifInterface.ORIENTATION_ROTATE_270;
            default:
                return ExifInterface.ORIENTATION_NORMAL;
        }
    }

    public static Bitmap getScaledBitmapForHeight(Bitmap bitmap, int outHeight) {
        float currentWidth = bitmap.getWidth();
        float currentHeight = bitmap.getHeight();
        float ratio = currentWidth / currentHeight;
        int outWidth = Math.round(outHeight * ratio);
        return getScaledBitmap(bitmap, outWidth, outHeight);
    }

    public static Bitmap getScaledBitmapForWidth(Bitmap bitmap, int outWidth) {
        float currentWidth = bitmap.getWidth();
        float currentHeight = bitmap.getHeight();
        float ratio = currentWidth / currentHeight;
        int outHeight = Math.round(outWidth / ratio);
        return getScaledBitmap(bitmap, outWidth, outHeight);
    }

    public static Pair<Integer, Integer> getUriImageWidthAndHeight(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getEncodedPath(), options);
        return new Pair<>(options.outWidth, options.outHeight);
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int outWidth, int outHeight) {
        int currentWidth = bitmap.getWidth();
        int currentHeight = bitmap.getHeight();
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.postScale(
                (float) outWidth / (float) currentWidth,
                (float) outHeight / (float) currentHeight
        );
        return Bitmap.createBitmap(bitmap, 0, 0, currentWidth, currentHeight, scaleMatrix, true);
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (Throwable ignored) {
        }
    }

    public static Bitmap resizeBitmapMeetLarge(Bitmap bitmap, int requestWidth, int requestHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratioWidth = (width * 1.0f) / requestWidth;
        float ratioHeight = (height * 1.0f) / requestHeight;
        float ratio = ratioWidth > ratioHeight ? ratioWidth : ratioHeight;
        if (ratio <= 1.0f) {
            return bitmap;
        }
        return Bitmap.createScaledBitmap(bitmap, (int) (width / ratio), (int) (height / ratio), true);
    }

    public static Bitmap resizeBitmapMeetSmall(Bitmap bitmap, int requestWidth, int requestHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratioWidth = (width * 1.0f) / requestWidth;
        float ratioHeight = (height * 1.0f) / requestHeight;
        float ratio = ratioWidth < ratioHeight ? ratioWidth : ratioHeight;
        if (ratio <= 1.0f) {
            return bitmap;
        }
        return Bitmap.createScaledBitmap(bitmap, (int) (width / ratio), (int) (height / ratio), true);
    }

    public static Bitmap resizeUri(Uri uri, int requestWidth, int requestHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getEncodedPath(), options);
        options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getEncodedPath(), options);
        if (bitmap.getWidth() > requestWidth || bitmap.getHeight() > requestHeight) {
            bitmap = Bitmap.createScaledBitmap(bitmap, requestWidth, requestHeight, true);
        }
        return bitmap;
    }

    public static Bitmap resizeImageFile(File file, int requestWidth, int requestHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (bitmap.getWidth() > requestWidth || bitmap.getHeight() > requestHeight) {
            bitmap = Bitmap.createScaledBitmap(bitmap, requestWidth, requestHeight, true);
        }
        return bitmap;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).
            long totalPixels = width * height / inSampleSize;
            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

}
