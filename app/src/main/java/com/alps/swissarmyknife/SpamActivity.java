package com.alps.swissarmyknife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by alna173017 on 3/23/2018.
 */

public class SpamActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spam);
    }

    public void spam(View v) {
        String phoneNumber = ((EditText) findViewById(R.id.txtPhone)).getText().toString();
        String textMessage = ((EditText) findViewById(R.id.txtTextMessage)).getText().toString();
        int spamCount = Integer.parseInt(
                // Guaranteed to work, it's a number only EditText
                ((EditText) findViewById(R.id.txtSpamCount)).getText().toString()
        );

        SmsManager textManager = SmsManager.getDefault();

        try {
            textManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
        } catch (SecurityException e) {
            Toast.makeText(this, "Need SMS permission to do this!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i=1;i<spamCount;i++) {
            textManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
        }
    }
}
