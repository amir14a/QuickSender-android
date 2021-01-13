package amirabbas.quickcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.DimenRes;

//import com.airbnb.lottie.LottieAnimationView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FF {
//    public static void lottieAnimToggle(LottieAnimationView lottieAnimationView) {
//        if (lottieAnimationView.isAnimating()) {
//            lottieAnimationView.pauseAnimation();
//            lottieAnimationView.setVisibility(View.GONE);
//        } else {
//            lottieAnimationView.playAnimation();
//            lottieAnimationView.setVisibility(View.VISIBLE);
//        }
//    }

    public static void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
                e.printStackTrace();
                if (e.getMessage() != null)
                    Log.i("aaLog", e.getMessage());
            }
        }
    }

    public static Bitmap resizeBitmap(Bitmap b, int maxSize) {
        if (Math.max(b.getWidth(), b.getHeight()) > maxSize) {
            if (b.getWidth() > b.getHeight())
                return Bitmap.createScaledBitmap(b, maxSize, maxSize * b.getHeight() / b.getWidth(), true);
            else
                return Bitmap.createScaledBitmap(b, maxSize * b.getWidth() / b.getHeight(), maxSize, true);
        }
        return b;
    }

    public static void playFadeInAnimation(View view) {
        AnimationSet a = new AnimationSet(true);
        ScaleAnimation s = new ScaleAnimation(0f, 1.2f, 0f, 1.2f, (float) view.getWidth() / 2, (float) view.getHeight() / 2);
        s.setDuration(120);
        s.setFillAfter(true);
        a.addAnimation(s);
        ScaleAnimation s2 = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, (float) view.getWidth() / 2, (float) view.getHeight() / 2);
        s2.setDuration(40);
        s2.setStartOffset(120);
        a.addAnimation(s2);
        AlphaAnimation alpha=new AlphaAnimation(0.2f,1f);
        alpha.setDuration(160);
        a.addAnimation(alpha);
        view.startAnimation(a);
    }

    public static int getPxVal(Context context, @DimenRes int val) {
        return context.getResources().getDimensionPixelSize(val);
    }

    public static boolean isEng(Context context) {
        return FF.getSavedValue("LANG", "en", context).equals("en");
    }

//    public static void lottieAnimHide(LottieAnimationView lottieAnimationView) {
//        lottieAnimationView.pauseAnimation();
//        lottieAnimationView.setVisibility(View.GONE);
//    }
//
//    public static void lottieAnimShow(LottieAnimationView lottieAnimationView) {
//        lottieAnimationView.playAnimation();
//        lottieAnimationView.setVisibility(View.VISIBLE);
//    }

    public static Boolean isDarkMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("darkMode", false);
    }

    public static void saveValue(String key, Boolean value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static void saveValue(String key, String value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static void saveValue(String key, int value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static Boolean getSavedValue(String key, Boolean defValue, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }

    public static String getSavedValue(String key, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public static String getSavedValue(String key, String defVal, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defVal);
    }

    public static HashMap<String, Boolean> getSettings(Context context) {
        HashMap<String, Boolean> map = new HashMap<>();
        map.put(Consts.sKaheshHajm, PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Consts.sKaheshHajm, true));
        map.put(Consts.sErsalAni, PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Consts.sErsalAni, true));
        map.put(Consts.sPDF, PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Consts.sPDF, false));
        map.put(Consts.sSaveIP, PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Consts.sSaveIP, true));
        return map;
    }

    public static Boolean getSetting(Context context, String key) {
        switch (key) {
            case Consts.sErsalAni:
            case Consts.sSaveIP:
            case Consts.sKaheshHajm:
                return PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean(key, true);
            case Consts.sPDF:
                return PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean(key, false);
            default:
                return false;
        }
    }

    public static int getSavedValue(String key, int defValue, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defValue);
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static String getDownloadCount(int downloads) {
        if (downloads < 50)
            return "<50";
        else if (downloads < 100)
            return "50";
        else if (downloads < 200)
            return "100";
        else if (downloads < 300)
            return "200";
        else if (downloads < 400)
            return "300";
        else if (downloads < 500)
            return "400";
        else if (downloads < 1000)
            return "500";
        else if (downloads < 2000)
            return "1K";
        else if (downloads < 3000)
            return "2K";
        else if (downloads < 4000)
            return "3K";
        else if (downloads < 5000)
            return "4K";
        else if (downloads < 7500)
            return "5K";
        else if (downloads < 10000)
            return "8K";
        else if (downloads < 15000)
            return "10K";
        else if (downloads < 20000)
            return "15K";
        else if (downloads < 30000)
            return "20K";
        else if (downloads < 50000)
            return "30K";
        else if (downloads < 100000)
            return "50K";
        else
            return ">100K";
    }
}
