package com.alps.swissarmyknife;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alna173017 on 4/13/2018.
 */

public class BluetoothLeService extends Service {
    public static final ParcelUuid GM_UUID =
            ParcelUuid.fromString("0000FE48-0000-1000-8000-00805f9b34fb");
    private final String TAG = getClass().getSimpleName();
    private Context mContext = null;
    private BluetoothAdapter mAdapter = null;
    private ArrayList<ScanFilter> mScanFilter = null;
    private final IBinder mBinder = new LocalBinder();
    private Handler[] mHandler= new Handler[5];

    private class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return new BluetoothLeService();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "we've binded");
        mContext = getApplicationContext();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public boolean init() {
        // Init mAdapter if havent already
        mAdapter = mAdapter == null ? BluetoothAdapter.getDefaultAdapter() :
                                        mAdapter;

        //
        mScanFilter = new ArrayList<>();
        mScanFilter.add(buildScanFilter());

        return true;
    }

    private ScanFilter buildScanFilter(int id) {
        // Need to be uppercase
        String filter = "010000";
        String cuid = "1122334455667788";
        filter += cuid;

        byte[] serviceData = new byte[] {
                0x01, (byte) 0xff, (byte) 0xFF,
                0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12,
                0x12,
                0x12,
                0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12};

        for (int i=0;i<11;i++) {
            if (filter.length()>=i*2) {
                serviceData[i] = (byte) (Integer.valueOf(filter.substring(i*2,i*2+2), 16) & 0xFF);
            }
        }

        serviceData[11] = (byte) id;
        serviceData[12] = (byte) (Integer.valueOf("02", 16) & 0xFF);

        ScanFilter scanFilter = new ScanFilter.Builder().setServiceData(GM_UUID, serviceData).build();
        return scanFilter;
    }

    public void startScan() {
        scanDevice(true);
    }

    public void stopScan() {
        scanDevice(false);
    }

    private void scanDevice(boolean enable) {
        BluetoothLeScanner bluetoothLeScanner = mAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
            return;
        }

        if (enable) {
            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bluetoothLeScanner.startScan(mScanFilter, settings, mScanFilter);
        } else {
            bluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            List<ParcelUuid> scanUuid = result.getScanRecord().getServiceUuids();

            if (scanUuid == null) {
                return;
            }

            byte[] data = result.getScanRecord().getServiceData(scanUuid.get(0));
            if (data == null) {
                return;
            }

            byte[] serviceData = new byte[8];
            for (int i=0;i<serviceData.length;i++) {
                serviceData[i] = (byte)(Integer.valueOf("1122334455667788".substring(i*2,i*2+2), 16) & 0xFF);
                if (serviceData[i] != data[i+3]) {
                    return;
                }
            }


            int deviceId = (int) data[11];
            int rssi = result.getRssi();
            broadcastUpdateRssi(deviceId, rssi);

            mHandler[deviceId].removeCallbacks(rssiTimeoutRunnable);
            mHandler.postDelayed(rssiTimeoutRunnable, 300);
        }
    };

    private Runnable rssiTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            broadcastUpdateRssi();
        }
    };

    private void broadcastUpdateRssi(int deviceId, int rssi) {
        Intent intent = new Intent("UPDATE_RSSI");
        intent.putExtra("RSSI", rssi);
        intent.putExtra("DEVICE_ID", deviceId);
        sendBroadcast(intent);
    }
}
