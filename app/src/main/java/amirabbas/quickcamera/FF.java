package amirabbas.quickcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import java.util.HashMap;

public class FF {

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

    public static void saveValue(String key, Boolean value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static void saveValue(String key, String value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
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

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
