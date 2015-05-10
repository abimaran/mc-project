package com.mobileproject.smartprofileselector;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobileproject.smartprofileselector.datasources.UserProfileDataSource;
import com.mobileproject.smartprofileselector.models.Profile;


public class UserProfileCreator extends ActionBarActivity {

    private Context context;

    private EditText txtProfileName;

    private Spinner selectRingMode;
    private Spinner selectNotificationMode;
    private Button selectRingTone;
    private Button selectNotificationTone;

    private SeekBar setRingVolume;
    private SeekBar setNotiVolume;
    private SeekBar setMediaVolume;

    private Button btnSaveProfile;
    private Button btnCancelProfile;

    private String ringtone;
    private String notificationTone;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_creator);

        // Set Application Context
        context = getApplicationContext();


        // Init UI Components
        txtProfileName = (EditText)findViewById(R.id.txtProfName);
        selectRingMode = (Spinner)findViewById(R.id.spinRingMode);
        selectRingTone = (Button)findViewById(R.id.btnSelectRingTone);
        selectNotificationTone = (Button)findViewById(R.id.btnSelectNotiTone);
        setRingVolume = (SeekBar)findViewById(R.id.seekBarRingtone);
        setRingVolume.setMax(10);
        setNotiVolume = (SeekBar)findViewById(R.id.seekBarNotification);
        setNotiVolume.setMax(10);
        setMediaVolume = (SeekBar)findViewById(R.id.seekBarMedia);
        setMediaVolume.setMax(10);

        btnSaveProfile = (Button)findViewById(R.id.btnSaveProfile);
        btnCancelProfile = (Button)findViewById(R.id.btnCancelProfile);
        btnCancelProfile = (Button)findViewById(R.id.btnCancelProfile);

        // Get available Ring tone list
        selectRingTone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_RINGTONE);
                intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        Uri.parse("/sdcard/media/audio/ringtone/"));

                startActivityForResult(intent, 0);
            }

        });

        // Get Notification tone list
        selectNotificationTone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        Uri.parse( "/sdcard/media/audio/ringtone/"));

                startActivityForResult(intent, 1);
            }

        });

        // Add and Save User Profile to System.
        btnSaveProfile.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                String profileName = txtProfileName.getText().toString();
                int ringMode = selectRingMode.getSelectedItemPosition();
                int ringtoneVolume = setRingVolume.getProgress();
                int notificationMode = 0;//selectNotiMode.getSelectedItemPosition();
                int notificationToneVolume = setNotiVolume.getProgress();
                int mediaVolume = setMediaVolume.getProgress();
                int activated = 0;

                UserProfileDataSource profileDataSource = new UserProfileDataSource(getApplicationContext());
                profileDataSource.open();
                Profile tempProfile = profileDataSource.createProfile(profileName, ringMode, ringtone,
                        ringtoneVolume, notificationMode, notificationTone, notificationToneVolume, mediaVolume, activated);
                profileDataSource.close();

                Toast toast = Toast.makeText(context, "Profile is saved successfully!", Toast.LENGTH_SHORT);
                toast.show();
                Log.i("Profile id", "" + tempProfile.getId());;

//                Intent timeIntent = new Intent(UserProfileCreator.this, ProfileMakerTimeActivity.class);
//                timeIntent.putExtra("profileId", tempProfile.getId());
//                ProfileMakerBasicActivity.this.startActivity(timeIntent);
            }
        });

        btnCancelProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                finish();
            }
        });







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile_creator, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (resultCode) {

            case RESULT_OK:

                // Setting ringtone to the variable.
                if(requestCode==0)
                {

                    // Send the ringtone that is picked in the Ringtone Picker Dialog
                    Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    // Send the output of the selected to a string
                    ringtone = uri.toString();

                    // Prints out the result in the console window
                    Log.i("Ring tone", "uri " + uri);

                }else
                {

                    // Set notification tone to variables.
                    // Send the ringtone that is picked in the Ringtone Picker Dialog
                    Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    // Send the output of the selected to a string
                    notificationTone = uri.toString();

                    // Prints out the result in the console window
                    Log.i("Notification tone", "uri " + uri);
                }
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}
