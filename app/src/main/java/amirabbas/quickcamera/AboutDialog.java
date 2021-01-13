package amirabbas.quickcamera;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

public class AboutDialog extends Dialog {


    public AboutDialog(@NonNull Context context) {
        super(context);
        Objects.requireNonNull(getWindow()).getAttributes().windowAnimations = R.style.AboutDialogAnimation;
        setContentView(R.layout.about);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView email = findViewById(R.id.email);
        TextView telegram = findViewById(R.id.telegram);
        email.setClickable(true);
        email.setMovementMethod (LinkMovementMethod.getInstance());
        telegram.setClickable(true);
        telegram.setMovementMethod (LinkMovementMethod.getInstance());
        View pay = findViewById(R.id.pay);
        telegram.setText(Html.fromHtml("Telegram: <a href=\"https://t.me/amir_a14\">@amir_a14</a>"));
        email.setText(Html.fromHtml("Email: <a href=\"mailto:a.abbasj@yahoo.com\">a.abbasj@yahoo.com</a>"));
        pay.setOnClickListener(view -> new AlertDialog.Builder(getContext()).setMessage("دمت گرم! فکر نمیکردم کسی این گزینه رو هم بزنه D: \n برای حمایت با مبلغ دلخواه میتونی از دکمه \"حمایت می‌کنم\" در پایین استفاده کنی، ضمنا درگاه مربوط به یکی دیگه از سایتای من به آدرس PES6.ir هست و متعلق به خودمه، پس در نتیجه موقع دیدن درگاه تعجب نکنید :)\nممنون :)")
                .setPositiveButton("حمایت میکنم!", (dialogInterface, i) -> getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://zarinp.al/pes6.ir"))))
                .setNegativeButton("برو بابا :|", null)
                .setCancelable(false)
                .setTitle("حمایت از من")
                .setPositiveButtonIcon(context.getResources().getDrawable(R.drawable.ic_check))
                .show());
    }

}
