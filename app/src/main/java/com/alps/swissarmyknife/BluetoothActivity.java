package com.alps.swissarmyknife;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by alna173017 on 3/27/2018.
 */

public class BluetoothActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {
    private ListView list;
    private BluetoothAdapter dealer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        BluetoothManager trapdog = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        dealer = trapdog.getAdapter();

        list = findViewById(R.id.lstBluetooth);
        ItemAdapter bt = new ItemAdapter(this);
        list.setAdapter(bt);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if device supports bluetooth
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Your device doesn't have BLE", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void scan(View v) {
        dealer.startDiscovery();

    }

    private class ItemAdapter extends BaseAdapter {
        Context context;
        private final String[] bullshit = {"Asd", "ASD", "asD", "aSd", "asd"};

        private class ViewHolder {
            TextView deviceName;
            TextView id;
        }

        ItemAdapter(Context c) {
            super();
            this.context = c;
        }

        @Override
        public int getCount() {
            return bullshit.length;
        }

        @Override
        public Object getItem(int i) {
            return bullshit[i % bullshit.length];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder v = null;

            if (convertView == null) {
                // View is not recycled, we need to create a new one
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_bluetooth, null);

                v = new ViewHolder();
                v.deviceName = convertView.findViewById(R.id.txtDeviceName);
                v.id = convertView.findViewById(R.id.txtDeviceId);

                v.deviceName.setText(bullshit[position]);
                v.id.setText(bullshit[position]);
            } else {
                // View is recycled, just manipulate its data
                v = (ViewHolder) convertView.getTag();
                v.deviceName.setText(bullshit[position]);
                v.id.setText(bullshit[position]);
            }
            convertView.setTag(v);
            return convertView;
        }
    }
}