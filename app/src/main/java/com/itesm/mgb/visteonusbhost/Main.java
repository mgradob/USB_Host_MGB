package com.itesm.mgb.visteonusbhost;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.codeandmagic.android.gauge.GaugeView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class Main extends Activity {

    private static int TIMEOUT = 100;
    private boolean forceClaim = true;

    //private GaugeView gaugeView1, gaugeView2;

    EditText cmdEt;
    Button getInfoBtn, sendCmdBtn, closeBtn;
    TextView mainTv;

    UsbDevice mUsbDevice;
    HashMap<String, UsbDevice> deviceList;
    UsbManager mUsbManager;

    int count = 0;

    private static final String ACTION_USB_PERMISSION = "com.itesm.mgb.visteonusbhost.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cmdEt = (EditText) findViewById(R.id.cmdEt);
        getInfoBtn = (Button) findViewById(R.id.getInfoBtn);
        sendCmdBtn = (Button) findViewById(R.id.sendCmdBtn);
        closeBtn = (Button) findViewById(R.id.closeBtn);
        mainTv = (TextView) findViewById(R.id.mainTv);

        /*GaugeOnTouchListener gaugeOnTouchListener1 = new GaugeOnTouchListener(this);
        GaugeOnTouchListener gaugeOnTouchListener2 = new GaugeOnTouchListener(this);
        gaugeView1 = (GaugeView) findViewById(R.id.gauge_view_1);
        gaugeView1.setOnTouchListener(gaugeOnTouchListener1);

        gaugeView2 = (GaugeView) findViewById(R.id.gauge_view_2);
        gaugeView2.setOnTouchListener(gaugeOnTouchListener2);

        GaugeFragment gaugeFragment1 = GaugeFragment.newInstance();
        addContentView(gaugeFragment1.getView(), null);*/

        getInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceInfo();
            }
        });

        sendCmdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

    private void close() {
        //count++;
        //gaugeView1.setTargetValue(count);
    }

    private void sendCommand() {
        byte[] dataOut, dataIn = new byte[100];

        dataOut = cmdEt.getText().toString().getBytes();

        for(HashMap.Entry<String, UsbDevice> entry : deviceList.entrySet()) {
            mUsbDevice = entry.getValue();
            Log.d("SEND_COMMAND", mUsbDevice.getDeviceName());

            int interfaceCount = mUsbDevice.getInterfaceCount();
            Log.d("SEND_COMMAND", "Interface count: " + interfaceCount);

            for(int x=0; x < interfaceCount; x++){
                UsbInterface mUsbInterface = mUsbDevice.getInterface(x);
                Log.d("SEND_COMMAND", "Interface: " + mUsbInterface.toString());
                int endpointCount = mUsbInterface.getEndpointCount();
                Log.d("SEND_COMMAND", "Endpoint Count:: " + endpointCount);

                for(int y=0; y<endpointCount; y++){
                    Log.d("SEND_COMMAND", "Endpoint Data:" + mUsbInterface.getEndpoint(y).getDirection());
                }
            }

            UsbInterface mUsbInterface = mUsbDevice.getInterface(1);
            UsbEndpoint mUsbEndpointOut = mUsbInterface.getEndpoint(0);    // Out endpoint.
            UsbEndpoint mUsbEndpointIn = mUsbInterface.getEndpoint(1);  //In endpoint.
            UsbDeviceConnection mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);

            mUsbDeviceConnection.claimInterface(mUsbInterface, forceClaim);
            int written = mUsbDeviceConnection.bulkTransfer(mUsbEndpointOut, dataOut, dataOut.length, TIMEOUT);
            Log.d("SEND_COMMAND", "Wrote: " + written + " bytes");
            Log.d("SEND_COMMAND", "Wrote: " + Arrays.toString(dataOut));
            int received = mUsbDeviceConnection.bulkTransfer(mUsbEndpointIn, dataIn, dataIn.length, TIMEOUT);

            Log.d("SEND_COMMAND", "Received: " + received + " bytes");
            Log.d("SEND_COMMAND", "Received: " + Arrays.toString(dataIn));
            mainTv.append("\nData received:\n" + new String(dataIn));
        }
    }

    private void getDeviceInfo() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> mDeviceIterator = deviceList.values().iterator();

        String deviceInfo = "";
        while (mDeviceIterator.hasNext()) {
            UsbDevice device = mDeviceIterator.next();
            deviceInfo += "\n" +
                    "DeviceID: " + device.getDeviceId() + "\n" +
                    "DeviceName: " + device.getDeviceName() + "\n" +
                    "DeviceClass: " + device.getDeviceClass() + "\n" +
                    "DeviceSubClass: " + device.getDeviceSubclass() + "\n" +
                    "VendorID: " + device.getVendorId() + "\n" +
                    "ProductID: " + device.getProductId() + "\n";
        }

        mainTv.setText(deviceInfo);


        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        for (HashMap.Entry<String, UsbDevice> entry : deviceList.entrySet()){
            UsbDevice aDevice = entry.getValue();
            if(!mUsbManager.hasPermission(aDevice)){
                mUsbManager.requestPermission(aDevice, mPermissionIntent);
            }
        }
    }
}
