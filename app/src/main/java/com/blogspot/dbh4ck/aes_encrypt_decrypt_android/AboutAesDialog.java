package com.blogspot.dbh4ck.aes_encrypt_decrypt_android;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * Created by DB on 18-02-2017.
 */

public class AboutAesDialog extends Dialog implements View.OnClickListener {
    Button OkBtn;
    public AboutAesDialog(Context context) {
        super(context);
    }

    public AboutAesDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AboutAesDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected void onStart() {
        super.onStart();
        setContentView(R.layout.about_aes);
        getWindow().setFlags(4,4);
        setTitle("About AES");

        OkBtn = (Button) findViewById(R.id.OkBtn);
        OkBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
    }
}
