package amirabbas.quickcamera;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

public class InputIp extends Dialog {


    public InputIp(@NonNull Context context) {
        super(context);
        setContentView(R.layout.ip_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        EditText editText = findViewById(R.id.ip);
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty())
                    Toast.makeText(context, "ورودیت خالیه که :|", Toast.LENGTH_SHORT).show();
                else {
                    ((MainActivity) ((Activity) context)).checkIp(editText.getText().toString().trim(), true);
                    dismiss();
                }
            }
        });
    }

}
