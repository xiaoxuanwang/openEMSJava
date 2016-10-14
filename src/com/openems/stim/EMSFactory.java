package com.openems.stim;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import com.openems.stim.ems.IEMSModule;
import com.openems.stim.ems.EMSModule;

class EMSFactory implements Observer {

	public static EMSFactory inst() {
		return new EMSFactory();	
	}
	
	private Context context;
	private IEMSModule cur_mod;
	
    public void setContext(Context context) {
        this.context = context;
		Log.v("EMS plugin", "context is set!");
    }

	public IEMSModule createModule() {
		if(null == this.cur_mod && null != this.context) {
			Object serv = this.context.getSystemService(Context.BLUETOOTH_SERVICE);
			cur_mod = new EMSModule((BluetoothManager) serv, "openEMS7");
			cur_mod.getBluetoothLEConnector().addObserver(this);
			cur_mod.connect();
		}
		return this.cur_mod;
	}

	public void showMessage(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }
	
	@Override
	public void update(Observable observable, Object data) {
		Log.v("EMS plugin", "Bluetooth event updated!");
		if(!(this.context instanceof Activity)) return;
		// Have to use this runOnUiThread to make the text show
		((Activity) this.context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // your stuff to update the UI
				if(cur_mod.isConnected()) {
					showMessage("Device connected");
				} else {
					showMessage("Device not working yet!");
				}
            }
        });
	}

}

