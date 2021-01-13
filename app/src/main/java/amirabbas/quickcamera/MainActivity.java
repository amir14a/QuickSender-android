package amirabbas.quickcamera;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.tom_roush.pdfbox.pdmodel.PDDocument;
//import com.tom_roush.pdfbox.pdmodel.PDPage;
//import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
//import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
//import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
//import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_QR_SCAN = 101;
    protected CardView send;
    RecyclerView filesRecycler;
    CardView qrScan;
    CardView dasti;
    CardView khodkar;
    LinearLayout ipButtons;
    ProgressBar progressBar;
    TextView iptxt;
    FloatingActionButton settingsBtn;
    FloatingActionButton cameraBtn;
    FloatingActionButton galleryBtn;
    FloatingActionButton infoBtn;
    Adapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
        if (isVPNOn()) {
            AlertDialog d = new AlertDialog.Builder(this)
                    .setMessage("به نظر می‌رسد که ارتباط VPN شما روشن است، با روشن بودن VPN برنامه نمی‌تواند کار کند، لطفا برای ادامه VPN را خاموش کنید.")
                    .setCancelable(false)
                    .setPositiveButton("خاموش کردن VPN", null)
                    .setNegativeButton("خاموش کردم", null)
                    .create();
            d.setOnShowListener(dialogInterface -> {
                d.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> {
                    if (isVPNOn()) {
                        Toast.makeText(MainActivity.this, "نکردی که :|", Toast.LENGTH_SHORT).show();
                    } else
                        d.dismiss();
                });
                d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                    Intent intent = new Intent("android.net.vpn.SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            });
            d.setOnDismissListener(dialogInterface -> {
                String s = FF.getSavedValue(Consts.LastIP, "0", MainActivity.this);
                if (!s.equals("0") && FF.getSetting(MainActivity.this, Consts.sSaveIP)) {
                    checkIp(s, false);
                }
            });
            d.show();
        } else {
            String s = FF.getSavedValue(Consts.LastIP, "0", this);
            if (!s.equals("0") && FF.getSetting(this, Consts.sSaveIP)) {
                checkIp(s, false);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        adapter = new Adapter(this);
        filesRecycler.setLayoutManager(new LinearLayoutManager(this));
        filesRecycler.setAdapter(adapter);
        adapter.setMessage("هیچ فایلی اضافه نشده است! لطفا برای اضافه کردن تصاویر از دکمه دوربین و یا گالری استفاده کنید.", Adapter.messageTypeNotLoading);
        playAnimation();
    }

    public void checkIp(String s, boolean isShowFailed) {
        progressBar.setVisibility(View.VISIBLE);
        String bef=iptxt.getText().toString();
        if (isShowFailed)
            iptxt.setText("در حال بررسی آیپی " + s);
        else
            iptxt.setText("در حال بررسی آخرین آیپی " + s);
        ipButtons.setVisibility(View.GONE);
        Call<ResponseBody> call = ApiClient.getInstanceForCheck("http://" + s + ":8787/").check();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        iptxt.setText(s);
                        Toast.makeText(MainActivity.this, "سرور پیدا شد!", Toast.LENGTH_SHORT).show();
                    });
                    Consts.baseURL = "http://" + s + ":8787/";
                    if (FF.getSetting(MainActivity.this, Consts.sSaveIP) && !Consts.baseURL.equals("0")) {
                        FF.saveValue(Consts.LastIP, s, MainActivity.this);
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isShowFailed)
                    iptxt.setText("ناموفق :(");
                else
                    iptxt.setText(bef);
                ipButtons.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void checkIp2(String s) {
        Call<ResponseBody> call = ApiClient.getInstanceForCheck("http://" + s + ":8787/").check();
        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    iptxt.setText(s);
                    Toast.makeText(MainActivity.this, "سرور پیدا شد!", Toast.LENGTH_SHORT).show();
                });
                Consts.baseURL = "http://" + s + ":8787/";
                if (FF.getSetting(MainActivity.this, Consts.sSaveIP) && !Consts.baseURL.equals("0")) {
                    FF.saveValue(Consts.LastIP, s, MainActivity.this);
                }
            }
        } catch (Exception e) {
        }
    }

    private void playAnimation() {
        View[] views = {settingsBtn, cameraBtn, galleryBtn, infoBtn};
        int delay = 150;
        final Handler handler = new Handler();
        for (int i = 0; i < views.length; i++) {
            {
                final int index = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = new TranslateAnimation(0, 0, 0, -100f);
                        animation.setRepeatCount(1);
                        animation.setRepeatMode(Animation.REVERSE);
                        animation.setDuration(300);
                        views[index].startAnimation(animation);
                        views[index].setClickable(true);
                    }
                }, delay);
                delay += 150;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            ArrayList<MyFile> files = new ArrayList<>();
            for (Image image : images) {
                MyFile file = new MyFile(image.getPath());
                files.add(file);
            }
            adapter.deleteMessage();
            adapter.addFiles(files);
            if (FF.getSetting(this, Consts.sErsalAni)) {
                adapter.send();
                send.setVisibility(View.GONE);
            } else
                send.setVisibility(View.VISIBLE);
        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            checkIp(result, true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        iptxt = (TextView) findViewById(R.id.iptxt);
        settingsBtn = (FloatingActionButton) findViewById(R.id.settings_btn);
        settingsBtn.setOnClickListener(MainActivity.this);
        cameraBtn = (FloatingActionButton) findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(MainActivity.this);
        galleryBtn = (FloatingActionButton) findViewById(R.id.gallery_btn);
        galleryBtn.setOnClickListener(MainActivity.this);
        infoBtn = (FloatingActionButton) findViewById(R.id.info_btn);
        infoBtn.setOnClickListener(MainActivity.this);
        filesRecycler = (RecyclerView) findViewById(R.id.filesRecycler);
        qrScan = (CardView) findViewById(R.id.qrScan);
        qrScan.setOnClickListener(MainActivity.this);
        dasti = (CardView) findViewById(R.id.dasti);
        dasti.setOnClickListener(MainActivity.this);
        khodkar = (CardView) findViewById(R.id.khodkar);
        khodkar.setOnClickListener(MainActivity.this);
        ipButtons = (LinearLayout) findViewById(R.id.ipButtons);
        send = (CardView) findViewById(R.id.send);
        send.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.settings_btn) {
            SettingDialog d = new SettingDialog(this);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    AnimationSet a = new AnimationSet(true);
                    ScaleAnimation s = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, (float) view.getWidth() / 2, (float) view.getHeight() / 2);
                    s.setRepeatCount(1);
                    s.setDuration(100);
                    s.setRepeatMode(Animation.REVERSE);
                    a.addAnimation(s);
                    RotateAnimation r = new RotateAnimation(0, 360, (float) view.getWidth() / 2, (float) view.getHeight() / 2);
                    r.setDuration(200);
                    r.setStartOffset(200);
                    a.addAnimation(r);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.startAnimation(a);
                        }
                    }, 200);
                }
            });
            d.setXY(view.getX(), view.getY());
            d.show();
        } else if (view.getId() == R.id.camera_btn) {
            if (!Consts.baseURL.equals("0")) {
                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                progressBar.setVisibility(View.GONE);
                Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        ImagePicker.cameraOnly().start(MainActivity.this);
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("این گزینه نیاز به دسترسی به دوربین و فایل ها دارد، لطفا بعد از دادن دسترسی مجددا امتحان کنید.")
                                .show();
                    }
                });
            } else
                Toast.makeText(this, "آیپی سرور مشخص نشده است!", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.gallery_btn) {
            if (!Consts.baseURL.equals("0")) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                progressBar.setVisibility(View.GONE);
                Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        ImagePicker.create(MainActivity.this).start();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("این گزینه نیاز به دسترسی به فایل ها دارد، لطفا بعد از دادن دسترسی مجددا امتحان کنید.")
                                .show();
                    }
                });
            } else
                Toast.makeText(this, "آیپی سرور مشخص نشده است!", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.info_btn) {
            AboutDialog d = new AboutDialog(this);
            d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    AnimationSet a = new AnimationSet(true);
                    ScaleAnimation s = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, (float) view.getWidth() / 2, (float) view.getHeight() / 2);
                    s.setRepeatCount(1);
                    s.setDuration(100);
                    s.setRepeatMode(Animation.REVERSE);
                    a.addAnimation(s);
                    RotateAnimation r = new RotateAnimation(0, 360, (float) view.getWidth() / 2, (float) view.getHeight() / 2);
                    r.setDuration(200);
                    r.setStartOffset(200);
                    a.addAnimation(r);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.startAnimation(a);
                        }
                    }, 200);
                }
            });
            d.show();
        } else if (view.getId() == R.id.qrScan) {
            Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
            startActivityForResult(i, REQUEST_CODE_QR_SCAN);
        } else if (view.getId() == R.id.dasti) {
            new InputIp(this).show();
        } else if (view.getId() == R.id.khodkar) {
            searchNetwork();
        } else if (view.getId() == R.id.send) {
            adapter.send();
        }
    }

    public boolean isVPNOn() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                Log.d("DEBUG", "IFACE NAME: " + iface);
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    public void searchNetwork() {
        final String TAG = "nstask";
        progressBar.setVisibility(View.VISIBLE);
        iptxt.setText("در حال پیدا کردن سرور...");
        ipButtons.setVisibility(View.GONE);
        final WeakReference<Context> mContextRef = new WeakReference<Context>(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Consts.baseURL.equals("0")) {
                    Log.d(TAG, "Let's sniff the network");
                    try {
                        Context context = mContextRef.get();
                        if (context != null) {
                            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            WifiInfo connectionInfo = wm.getConnectionInfo();
                            int ipAddress = connectionInfo.getIpAddress();
                            String ipString = Formatter.formatIpAddress(ipAddress);
                            Log.d(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
                            Log.d(TAG, "ipString: " + String.valueOf(ipString));
                            String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                            Log.d(TAG, "prefix: " + prefix);
                            boolean baraks = false;
                            if (ipString.equals("0.0.0.0") && InetAddress.getByName("192.168.43.1").isReachable(200)) {
                                prefix = "192.168.43.";
                                baraks = true;
                            }
                            if (ipString.equals("0.0.0.0") && InetAddress.getByName("192.168.42.129").isReachable(200)) {
                                prefix = "192.168.42.";
                                baraks = true;
                            }
                            int start = 0;
                            if (prefix.equals("192.168.1"))
                                start = 2;
                            for (int i = baraks ? 255 : start; baraks ? i >= start : i <= 255; i = baraks ? i - 1 : i + 1) {
                                if (!Consts.baseURL.equals("0"))
                                    break;
                                String testIp = prefix + String.valueOf(i);
                                InetAddress address = InetAddress.getByName(testIp);
                                boolean reachable = address.isReachable(100);
                                runOnUiThread(() -> {
                                    iptxt.setText("در حال بررسی آیپی " + testIp);
                                });
                                if (reachable) {
                                    checkIp2(testIp);
                                }
                            }
                        }
                    } catch (Throwable t) {
                        Log.e(TAG, "Well that's not good.", t);
                    }
                }
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                });
                if (Consts.baseURL.equals("0")) {
                    runOnUiThread(() -> {
                        iptxt.setText("سرور پیدا نشد :(");
                        ipButtons.setVisibility(View.VISIBLE);
                    });
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (adapter.checkInProgress())
            new AlertDialog.Builder(this)
                    .setMessage("فایل های شما در حال ارسال می‌باشند! آیا میخواهید خارج شوید؟ در اینصورت ارسال فایل ها متوقف خواهد شد.")
                    .setPositiveButton("بله", (dialogInterface, i) -> MainActivity.super.onBackPressed())
                    .setNegativeButton("خیر", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        else
            super.onBackPressed();
    }
}


