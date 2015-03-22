package com.profilemaker.service;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSHandler {

	private static final String TAG = null;
	public Context context;
	protected String body;
	
	
	// Sms sending function.....
	public void sendSms(String phnNo, String body) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phnNo, null, body, null, null);
		Log.d("SMS", "SMS sent...");
	}
	
	protected String readSMS(Context mContext){
//		listitem=(ListView)findViewById(R.id.ListView);
		context = mContext;
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			Log.e("error","Time is not enough to read sms");
		}
        Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
        //List<String> messages = new ArrayList<String>();

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(mSmsQueryUri, null, null, null, null);
            if (cursor == null) {
                Log.i(TAG, "cursor is null. uri: " + mSmsQueryUri);
            }
            
          //  for (boolean hasData = cursor.moveToLast(); hasData; hasData = cursor.moveToNext()) {
            cursor.moveToFirst();    
            body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                //messages.add(body);
                
         //   }
            
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }

 //       listitem.setAdapter(new ArrayAdapter<String>(ReadMessage.this, android.R.layout.simple_list_item_1,messages));	
		return body;
	}
	
	//check whether message has 'urgent!' keyword.........
	public boolean checkSMS(String msg, String keyword){
		
		if(msg.equalsIgnoreCase(keyword))
			return true;
		else
			return false;
	}
	
}
