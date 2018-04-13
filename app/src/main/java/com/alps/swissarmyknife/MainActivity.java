package com.alps.swissarmyknife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void switchView(View view) {
        if (view.equals(findViewById(R.id.btnSpam))) {
            Intent intent = new Intent(this, SpamActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
        }
    }
}
