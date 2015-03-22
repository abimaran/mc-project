package com.profilemaker.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import com.profilemaker.activity.R;
import com.profilemaker.dao.TimePeriodDataSource;
import com.profilemaker.model.Period;
import com.profilemaker.model.TimePeriod;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ProfileMakerTimeActivity extends Activity{

	private Context context;
	private Intent intent;
	
	private Button startTime;
	private Button endTime;
	private TextView mTimeDisplay;
	private Button dayPicker;
	private TextView daysDisplay;
	private Button setBackPeriod;
	private Button setNextPeriod;
	private Button addPeriod;
	private Button delPeriod;
	private Button saveTimes;
	private Button cancelTimes;
	
	private int periodId = 0;
	private ArrayList<String> days;
	private int mHour,mHourStart, mHourEnd;
	private int mMinute, mMinuteStart, mMinuteEnd;
	private ArrayList<Period> periods;
	
	
	static final int TIME_DIALOG_ID = 1;
	private short start = 0;
	private short day = 0;
	private int token = 0;
	
	
	protected CharSequence[] _options = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
	protected boolean[] _selections =  new boolean[_options.length];
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_maker_time);
        context = getApplicationContext();
        intent = getIntent();
        
        startTime = (Button) findViewById(R.id.btnStartTime);
        endTime = (Button) findViewById(R.id.btnendTime);
        mTimeDisplay =(TextView)findViewById(R.id.txtViewTimes);
        dayPicker = (Button)findViewById(R.id.btnSelectDays);
        daysDisplay =(TextView)findViewById(R.id.txtViewDays);
        setBackPeriod = (Button)findViewById(R.id.btnBackPeriod);
        setNextPeriod = (Button)findViewById(R.id.btnNextPeriod);
        addPeriod = (Button)findViewById(R.id.btnAddPeriod);
        delPeriod = (Button)findViewById(R.id.btnDelPeriod);
        saveTimes = (Button)findViewById(R.id.btnSaveTime);
        cancelTimes = (Button)findViewById(R.id.btnCancelTime);
        
        periods = new ArrayList<Period>();
       
        //Pick time's click event listener
        startTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                
                showDialog(TIME_DIALOG_ID);
                start = 1;
            }

        });

        final Calendar cs = Calendar.getInstance();
        mHour = cs.get(Calendar.HOUR_OF_DAY);
        mMinute = cs.get(Calendar.MINUTE);
        
        endTime.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               
                showDialog(TIME_DIALOG_ID);
                start = 0;
            }
        });    
        
        dayPicker.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                day = 1;
                showDialog(0);
                day = 0;
            }
        });    
        
        setNextPeriod.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(token<(periods.size()-1)){
					token++;
					Period tokenPeriod = periods.get(token);
					updateDays(tokenPeriod.getDays());
					updatestarttime(tokenPeriod.getStartHour(), tokenPeriod.getStartMinute());
					updateendtime(tokenPeriod.getEndHour(), tokenPeriod.getEndMinute());
										
					
				}
			}
		});
        
        setBackPeriod.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(token>0){
					token--;
					Period tokenPeriod =  periods.get(token);
					updateDays(tokenPeriod.getDays());
					updatestarttime(tokenPeriod.getStartHour(), tokenPeriod.getStartMinute());
					updateendtime(tokenPeriod.getEndHour(), tokenPeriod.getEndMinute());					
				}				
			}
		});
        
        addPeriod.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(_selections.length>0){
					periodId ++;
					Period befPeriod = new Period(periodId, days, mHourStart, mHourEnd, mMinuteStart,mMinuteEnd);
					periods.add(befPeriod);
					token = periods.size()-1;
					
					Toast toast = Toast.makeText(context, "Period is added successfully!", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
        
        delPeriod.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				periods.remove(token);
				if(token==periods.size())
					token--;
				Period tokenPeriod = periods.get(token);
				updateDays(tokenPeriod.getDays());
				updatestarttime(tokenPeriod.getStartHour(), tokenPeriod.getStartMinute());
				updateendtime(tokenPeriod.getEndHour(), tokenPeriod.getEndMinute());
					
				Toast toast = Toast.makeText(context, "Period is deleted successfully!", Toast.LENGTH_SHORT);
				toast.show();
			}
		});   
        
        saveTimes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				//check whether periods arraylist is empty....................
				if(!periods.isEmpty()){
					Iterator<Period> itrPeriods = periods.iterator();
					TimePeriodDataSource timePeriodDataSource = new TimePeriodDataSource(context);
					timePeriodDataSource.open();
					int profileId = intent.getIntExtra("profileId", 0);
					
					//Go through periods arraylist..................
					while(itrPeriods.hasNext()){
						Period tempPeriod = itrPeriods.next();
						
						if(!tempPeriod.getDays().isEmpty()){
							Iterator<String> itrDays = tempPeriod.getDays().iterator();
							int startHour = tempPeriod.getStartHour();
							int startMinute = tempPeriod.getStartMinute();
							int endHour = tempPeriod.getEndHour();
							int endMinute = tempPeriod.getEndMinute();
							
							//Go through Days arraylist....................
							while(itrDays.hasNext()){
								String day = itrDays.next();
								
								TimePeriod tempTimePeriod = timePeriodDataSource.createTimePeriod(
										profileId, day, startHour, startMinute, endHour, endMinute);
								Log.i("Days", tempTimePeriod.getDay());
								
								Toast toast = Toast.makeText(context, "Time Details saved Successfully!", Toast.LENGTH_SHORT);
								toast.show();
							}
						}else{
							Toast toast = Toast.makeText(context, "Please select days!", Toast.LENGTH_SHORT);
							toast.show();
						}
					}
					
					timePeriodDataSource.close();
				}else{
					Toast toast = Toast.makeText(context, "Please select days and time periods!", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				Intent locationIntent = new Intent(ProfileMakerTimeActivity.this,ProfileMakerGPSActivity.class);
				locationIntent.putExtra("profileId", intent.getIntExtra("profileId", 0));
				startActivity(locationIntent);
				
			}
			
		});
        
        cancelTimes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent locationIntent = new Intent(ProfileMakerTimeActivity.this,ProfileMakerGPSActivity.class);
				locationIntent.putExtra("profileId", intent.getIntExtra("profileId", 0));
				startActivity(locationIntent);
			}
		});
	}
	
	//-------------------------------------------update start time----------------------------------------//    
	public void updatestarttime(int mHour, int mMinute)
	{
		mHourStart = mHour;
		mMinuteStart = mMinute;
		String timeAdded = (String)mTimeDisplay.getText();
		
		//show start time to the user before add period............................. 
		if(timeAdded.length() <= 14){
			mTimeDisplay.setText(
	            new StringBuilder().append("Times:").append(pad(mHourStart)).append(":")
	                    .append(pad(mMinuteStart)).append(" - ")); 
		}else{
			String temp = timeAdded.substring(14);
			mTimeDisplay.setText(
		            new StringBuilder().append("Times:")
		                    .append(pad(mHourStart)).append(":")
		                    .append(pad(mMinuteStart)).append(" - ").append(temp)); 
		}
	    //those should be entered into database.....................
	}
	
	//-------------------------------------------update end time----------------------------------------//    
	public void updateendtime(int mHour, int mMinute)
	{
		mHourEnd = mHour;
		mMinuteEnd = mMinute;
		String timeAdded = (String)mTimeDisplay.getText();
		String temp = timeAdded.substring(0,14);
		
	    mTimeDisplay.setText(
	            new StringBuilder().append(temp)
	                    .append(pad(mHourEnd)).append(":")
	                    .append(pad(mMinuteEnd)).append(",")); 
	    
	    //those should be entered into database.....................
	}

	//-------------------------------------------update days---------------------------
	public void updateDays(ArrayList<String> days) {
		
		daysDisplay.setText(new StringBuilder().append("Days: ").append(days));
		
		//those should be entered into database.....................
	}
	
	private static String pad(int c) {
	            if (c >= 10)
                   return String.valueOf(c);
	            else
	               return "0" + String.valueOf(c);
    }
	  
	// Time picker dialog generation
		 private TimePickerDialog.OnTimeSetListener mTimeSetListener =
		        new TimePickerDialog.OnTimeSetListener() {
	            @Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		               mHour = hourOfDay;
		               mMinute = minute;
		               
		               switch (start) {
					case 1:
						updatestarttime(mHour, mMinute);
						break;
					case 0:
						updateendtime(mHour, mMinute);
						break;
					default:
						break;
					}		              
		        }
		  };
	  
	  @Override
	    protected Dialog onCreateDialog(int id) {
	        switch (day) {
			case 0:
				return new TimePickerDialog(this,mTimeSetListener, mHour, mMinute, false);
				
			case 1:
				return new AlertDialog.Builder( this )
	     	       .setTitle( "Days of Week" )
	     	       .setMultiChoiceItems( _options, _selections, new DialogSelectionClickHandler() )
	     	       .setPositiveButton( "OK", new DialogButtonClickHandler() )
	     	       .create();
				
			default:
				break;
			}
	            
	        return null; 	            
	    
	  }
	  
	  public class DialogButtonClickHandler implements DialogInterface.OnClickListener
	  {
	  	@Override
		public void onClick( DialogInterface dialog, int clicked ) {
	  		switch( clicked ) {
	  			case DialogInterface.BUTTON_POSITIVE:
	  				printSelectedDays();
	  				break;
	  		}
	  	}
	  }
	  
	  public class DialogSelectionClickHandler implements OnMultiChoiceClickListener {

			@Override
			public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}

	  }
	  
	  protected void printSelectedDays()
	  {
		  days =new ArrayList<String>();
		  
	          for( int i = 0; i < _options.length; i++ ){
	  	          Log.i( "ME", _options[ i ] + " selected: " + _selections[i] );
	  	      
	  	          if(_selections[i]){
	  	        	  days.add(""+_options[i]);        	  
	  	          }
	          }
	          updateDays(days);
	  }
}
