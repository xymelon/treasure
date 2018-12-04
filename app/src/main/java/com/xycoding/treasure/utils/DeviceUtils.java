package com.xycoding.treasure.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;

/**
 * Created by xuyang on 2016/6/15.
 */
public class DeviceUtils {

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isGranted(@NonNull Context context, String permission) {
        return !isMarshmallow()
                || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isRevoked(@NonNull Context context, String permission) {
        return isMarshmallow()
                && context.getPackageManager().isPermissionRevokedByPolicy(permission, context.getPackageName());
    }

    private static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void openCamera(@NonNull Fragment fragment, @NonNull Uri uri, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openGallery(@NonNull Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     *
     * @param window
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void toggleHideyBar(Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int newUiOptions = window.getDecorView().getSystemUiVisibility();
        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Navigation bar hiding:  Backwards compatible to ICS.
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        window.getDecorView().setSystemUiVisibility(newUiOptions);
    }

    public static void hideSystemUI(Window window) {
        int newUiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        window.getDecorView().setSystemUiVisibility(newUiOptions);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public static void showSystemUI(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static int getScreenHeight(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getStatusBarHeight(@NonNull Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int x = (Integer) field.get(object);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dp2px(context, 24);
    }

    public static int dp2px(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static boolean isMeizu() {
        return Build.BRAND.equalsIgnoreCase("Meizu");
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    public static int getSoftKeyboardHeight(@NonNull Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        //计算普通键盘高度
        int heightKeyboard = decorView.getHeight() - rect.bottom;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK Level >= 21 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            heightKeyboard = heightKeyboard - getSoftButtonsBarHeight(activity);
        }
        return heightKeyboard;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    public static int getSoftButtonsBarHeight(@NonNull Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                final DisplayMetrics metrics = new DisplayMetrics();
                //获取的可能不是屏幕的真实高度
                windowManager.getDefaultDisplay().getMetrics(metrics);
                final int usableHeight = metrics.heightPixels;
                //获取当前屏幕的真实高度
                windowManager.getDefaultDisplay().getRealMetrics(metrics);
                int realHeight = metrics.heightPixels;
                if (realHeight > usableHeight) {
                    return realHeight - usableHeight;
                }
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    public static String getCpuInfoHardware() {
        BufferedReader bufferedReader = null;
        try {
            final FileReader fileReader = new FileReader("/proc/cpuinfo");
            bufferedReader = new BufferedReader(fileReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.startsWith("Hardware")) {
                    return str;
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                //do nothing.
            }
        }
    }

    public static boolean autoRotateSetting(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }
    }

    public static int screenWidthPixels() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int screenHeightPixels() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
