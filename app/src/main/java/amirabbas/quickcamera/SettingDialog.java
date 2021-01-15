package amirabbas.quickcamera;

import android.app.Dialog;
import android.content.Context;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Objects;

public class SettingDialog extends Dialog {
    protected Switch swKaheshHajm;
    protected Switch swSendFast;
    protected Switch swErsalPDF;
    protected Switch swSaveLastIP;
    float x = 0, y = 0;

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public SettingDialog(@NonNull Context context) {
        super(context);
        Objects.requireNonNull(getWindow()).getAttributes().windowAnimations = R.style.SettingDialogAnimation;
        setContentView(R.layout.setting_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView();

    }

    private void initView() {
        swKaheshHajm = findViewById(R.id.swKaheshHajm);
        swSendFast = findViewById(R.id.swSendFast);
        swErsalPDF = findViewById(R.id.swErsalPDF);
        swSaveLastIP = findViewById(R.id.swSaveLastIP);
        swErsalPDF.setOnCheckedChangeListener((compoundButton, b) -> {
            boolean isChecked = compoundButton.isChecked();
            if (isChecked) {
                if (!swKaheshHajm.isChecked())
                    swKaheshHajm.setChecked(true);
            }
            FF.saveValue(Consts.sPDF, isChecked, getContext());
        });
        swKaheshHajm.setChecked(FF.getSetting(getContext(), Consts.sKaheshHajm));
        swErsalPDF.setChecked(FF.getSetting(getContext(), Consts.sPDF));
        swSendFast.setChecked(FF.getSetting(getContext(), Consts.sErsalAni));
        swSaveLastIP.setChecked(FF.getSetting(getContext(), Consts.sSaveIP));
        swKaheshHajm.setOnCheckedChangeListener((compoundButton, b) -> {
            if(swErsalPDF.isChecked()){
                Toast.makeText(getContext(), "در هنگام روشن بودن گزینه \"ارسال به صورت پی‌دی‌اف\"، نمی‌توانید \"کاهش حجم تصاویر\" را خاموش کنید.", Toast.LENGTH_LONG).show();
                compoundButton.setChecked(true);
            }
            FF.saveValue(Consts.sKaheshHajm, compoundButton.isChecked(), getContext());
        });
        swSendFast.setOnCheckedChangeListener((compoundButton, b) -> {
            FF.saveValue(Consts.sErsalAni, compoundButton.isChecked(), getContext());
        });
        swSaveLastIP.setOnCheckedChangeListener((compoundButton, b) -> {
            FF.saveValue(Consts.sSaveIP, compoundButton.isChecked(), getContext());
        });
    }
}
