package com.profilemaker.service;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.profilemaker.activity.R;
import com.profilemaker.dao.PatternDataSource;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Contacts.People;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

public class AutomaticSMSSender extends BroadcastReceiver{

	private static final String TAG = null;
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private NotificationManager smsNotificationManager;
	private int SIMPLE_NOTFICATION_ID;
	private Context mContext;
	private ITelephony telephonyService;
	private String calledNo;
	private boolean needAutoMessage;
	private String keyword;
	private String messageBody;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		//Log.w("Bluetooth", "request came .............");
		
		// Make the NotificationManager object for user....
		smsNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Get the phone mode....
		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		SMSHandler smsHandler = new SMSHandler();
		
		// get the value from shared preferences..
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		needAutoMessage = sharedPref.getBoolean("pref_key_activate_automessage", false);
		messageBody = sharedPref.getString("pref_key_message_body", "")+" If urgent message me with '"+
						sharedPref.getString("pref_key_keyword", "")+"' keyword";
		keyword = sharedPref.getString("pref_key_keyword", "");
		
		String action = intent.getAction();
		if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {

			//check whether call is coming only not rejecting the call....
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_RINGING)) {
				
				Log.w("Auto message", ""+sharedPref.getBoolean("pref_key_activate_automessage", false));		

				//check whether phone is silent or vibrate.....
				if (needAutoMessage && ((am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) || (am
								.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE))) {
					
					//Send the SMS to caller to reply if urgent......
					
					calledNo = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					smsHandler.sendSms(calledNo,messageBody);
				
					//call this method to create ITelephony instance......
					getTeleService();
					
					//block the call when phone is silent or vibrate.....
						try {
							telephonyService.endCall();
						} catch (RemoteException e) {
							
							e.printStackTrace();
						}
					
					Notification notifyDetails = new Notification(
							R.drawable.marker, "SMS has sent!",
							System.currentTimeMillis());
					PendingIntent smsIntent = PendingIntent.getActivity(
							context, 0, new Intent(Intent.ACTION_VIEW,
									People.CONTENT_URI), 0);
					notifyDetails.setLatestEventInfo(context,
							"SMS has sent....", "Click on me to view Contacts",
							smsIntent);
					notifyDetails.flags |= Notification.FLAG_AUTO_CANCEL;

					// Showing notification...
					smsNotificationManager.notify(SIMPLE_NOTFICATION_ID,
							notifyDetails);
					Log.i(getClass().getSimpleName(),
							"Successfully sent the message..");
				}
			}			
		}
		
		if(action.equalsIgnoreCase(SMS_RECEIVED)){
			Log.w("fdgfg", "SMS has come .............");
			
			//returns the sms body directly as string.....
			String sms = smsHandler.readSMS(mContext);
			Log.w("abdfg", sms);
			
			if(smsHandler.checkSMS(sms, keyword)){
				Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(4000);
				Log.w("abdfg", "vibrating.................");
			}			
		}
		
		if(action.equalsIgnoreCase(AudioManager.RINGER_MODE_CHANGED_ACTION)){
			
			ModeTracker modeTracker = new ModeTracker();
			BluetoothPattern blutoothPattern = new BluetoothPattern(context);
			modeTracker.insertPattern(context);
			
			//write the current mode into myMode file....
			modeTracker.writeMode(context);
			
			//change the device name as current mode.....
			//blutoothPattern.changeDeviceName();
			//Log.i("Bluetooth", "device name changed.....!!");
			//LocationTracker loc = new LocationTracker();
			//loc.changeProfileOnBluetooth(context);
		}
		
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {	
			Log.i("Descovery result", "Action found");
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Add the name and address to an array adapter to show in a ListView
           // Log.i("Descovery result", device.getName());
            
            if(device.getName().equals("0")||device.getName().equals("1")||device.getName().equals("2")){ 
            	BluetoothPattern.modes.add(Integer.parseInt(device.getName()));
            	 Log.i("Descovery result", ""+BluetoothPattern.modes.get(0));
            }
		}
		
		if(action.equalsIgnoreCase(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
			Log.i("Bluetooth","File receives........");
			BluetoothPattern bluetoothPattern = new BluetoothPattern(context);
			bluetoothPattern.readRequset(intent);
			bluetoothPattern.readMode();
		}		
	}
	
	private void getTeleService() {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            Log.v(TAG, "Get getTeleService...");
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
        }
	}

	public static void vibrate(Context context, boolean vibrate, Intent intent){
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		
		while(vibrate&&intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
				TelephonyManager.EXTRA_STATE_RINGING)){		
			
			v.vibrate(2000);
			Log.i("Vibrating","vibrating..............");
			try {
				Thread.sleep(1000);
				Log.i("Sleeping","sleeping..............");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
	}

}
