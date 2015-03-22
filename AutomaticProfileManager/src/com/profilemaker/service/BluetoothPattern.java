package com.profilemaker.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class BluetoothPattern{
    
	private Context context;
	public static ArrayList<Integer> modes = new ArrayList<Integer>();
	private BluetoothAdapter mBluetoothAdapter;
	private AudioManager am;
	
	public BluetoothPattern(Context context){
		this.context = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		//modes =new ArrayList<Integer>();
	}
	
	public void getModes(){
		int noOf0s=0, noOf1s=0, noOf2s=0;
		Log.i("Bluetooth", "getModes...");
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			
			if (mBluetoothAdapter.isEnabled()) {
				
				mBluetoothAdapter.startDiscovery();
			
				if(!mBluetoothAdapter.isDiscovering()){
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					mBluetoothAdapter.cancelDiscovery();
					Log.i("Discovering", ""+modes.get(0));
					Iterator<Integer> itrModes = modes.iterator();
					while(itrModes.hasNext()){
						switch(itrModes.next()){
						case 0:
							noOf0s++;
							break;
						case 1:
							noOf1s++;
							break;
						case 2:
							noOf2s++;
							break;
						}			
					}
					Log.i("Bluetooth", ""+noOf0s+","+ noOf1s+","+ noOf2s);
					
					//change the ringer mode using larger no of mode of mobiles....................
					 
					if(modes.size()>0){
						if((noOf0s>=noOf1s)&&(noOf0s>=noOf2s))
							am.setRingerMode(0);
						else if((noOf1s>=noOf0s)&&(noOf1s>=noOf2s))
							am.setRingerMode(1);
						else if((noOf2s>=noOf0s)&&(noOf2s>=noOf1s))
							am.setRingerMode(2);
						Log.i("Bluetooth","Ringer mode changed...");
					}					
				}
			}
		}
	}
	
	public void changeDeviceName(){
		
		//mBluetoothAdapter.setName(""+am.getRingerMode());
	}
	
	public void sendRequest(){
		
		 
		if (mBluetoothAdapter != null) {
			
			if (mBluetoothAdapter.isEnabled()) {
		
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
				 //If there are paired devices
				if (pairedDevices.size() > 0) {
			        
				     //Loop through paired devices
				    for (BluetoothDevice device : pairedDevices) {			    	
				        Log.i("Bluetooth",""+device.getName() + " " + device.getAddress());
				       
				        if(device.getBondState()==device.BOND_BONDED){

				            Log.d("Bluetooth ",device.getName());
				           
				            String filePath = Environment.getExternalStorageDirectory().toString() + "/Mode/request.txt";
				            
				            ContentValues values = new ContentValues();
				            values.put(BluetoothShare.URI, Uri.fromFile(new File(filePath)).toString());
				            values.put(BluetoothShare.DESTINATION, device.getAddress());
				            values.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND);
				            Long ts = System.currentTimeMillis();
				            values.put(BluetoothShare.TIMESTAMP, ts);
				            Uri contentUri = context.getContentResolver().insert(BluetoothShare.CONTENT_URI, values);
				            
				            Log.d("Bluetooth ","File has sent...........");			            				           
				        }
				    }
				}
			}
		}
	}
	
	public void readRequset(Intent intent){
		File request = new File(Environment.getExternalStorageDirectory()+"/bluetooth", "request.txt");
		
		if(request.exists()){
			try {
				FileReader fileReader = new FileReader(request);
				BufferedReader br = new BufferedReader(fileReader);
				if(br.readLine().equalsIgnoreCase("mode")){
					BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String btAddress = remoteDevice.getAddress();
					sendMode(btAddress);					
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException ioe){
				ioe.printStackTrace();
			}			
			request.delete();			
		}		
	}
	
	public void sendMode(String btAddress){
		String filePath = Environment.getExternalStorageDirectory().toString() + "/Mode/myMode.txt";
        
        ContentValues values = new ContentValues();
        values.put(BluetoothShare.URI, Uri.fromFile(new File(filePath)).toString());
        values.put(BluetoothShare.DESTINATION, btAddress);
        values.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND);
        Long ts = System.currentTimeMillis();
        values.put(BluetoothShare.TIMESTAMP, ts);
        Uri contentUri = context.getContentResolver().insert(BluetoothShare.CONTENT_URI, values);
        Log.i("Bliutooth","Mode sent.....!!");
	}
	
	public void readMode(){
		ArrayList<Integer> modes = new ArrayList<Integer>();
		int noOf0s = 0, noOf1s = 0, noOf2s = 0;
		int i = 1;
		try {
			//thread sleep for writing data into myMode files using bluetooth connection...
			Thread.sleep(8000);	
			
			String myMode = "myMode.txt";
			
			
			//match the myMode file name and read the mode from file to add arraylist.....
			while(true){
				if(i>=2){
					myMode = "myMode_"+i+".txt";
				}
				File myModeFile = new File(Environment.getExternalStorageDirectory()+"/bluetooth",myMode);
				if(myModeFile.exists()){
					FileReader fileReader = new FileReader(myModeFile);
					BufferedReader br = new BufferedReader(fileReader);
					modes.add(Integer.parseInt(br.readLine().toString()));
				}else
					break;
				i++;
				myModeFile.delete();
			}		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//count the no of mobiles in each mode.......
		Iterator<Integer> itrModes = modes.iterator();
		while(itrModes.hasNext()){
			switch(itrModes.next()){
			case 0:
				noOf0s++;
				break;
			case 1:
				noOf1s++;
				break;
			case 2:
				noOf2s++;
				break;
			}			
		}
		Log.i("Bluetooth", ""+noOf0s+","+ noOf1s+","+ noOf2s);
		
		//change the ringer mode using larger no of mode of mobiles....................
		AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(i>1){
			if((noOf0s>=noOf1s)&&(noOf0s>=noOf2s))
				am.setRingerMode(0);
			else if((noOf1s>=noOf0s)&&(noOf1s>=noOf2s))
				am.setRingerMode(1);
			else if((noOf2s>=noOf0s)&&(noOf2s>=noOf1s))
				am.setRingerMode(2);
		}
		Log.i("Bluetooth","Ringer mode changed...");
		
	}
	
	/*@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}*/
}
