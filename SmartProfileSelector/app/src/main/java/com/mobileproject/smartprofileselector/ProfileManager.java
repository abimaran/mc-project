package com.mobileproject.smartprofileselector;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.mobileproject.smartprofileselector.datasources.UserProfileDataSource;
import com.mobileproject.smartprofileselector.models.Profile;
import com.mobileproject.smartprofileselector.service.ProfileActivator;

/**
 * Main Activity Load on when application Start up
 */
public class ProfileManager extends ActionBarActivity {

    /**
     *
     */
    private Context context;

    private Button btnAddProfile;
    private Button btnDeleteProfile;

    private ListView listView;
    ArrayAdapter<String> profileList;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manager);

        // Set Application Context
        context = getApplicationContext();

        // Init ui components
        btnAddProfile = (Button)findViewById(R.id.btnAddProfile);
        btnDeleteProfile = (Button)findViewById(R.id.btnDeleteProfile);
        listView = (ListView)findViewById(R.id.profileList);

        // Load profile List
        loadProfileList();


        // Set event listeners for Buttons
        btnAddProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent profileIntent = new Intent(ProfileManager.this,UserProfileCreator.class);
                startActivity(profileIntent);
            }
        });

        btnDeleteProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        // Set onItem Select Listener for List Vew
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                UserProfileDataSource profileDataSource = new UserProfileDataSource(context);
                profileDataSource.openReadable();
                Profile profile = profileDataSource.getProfile(parent.getItemAtPosition(position).toString());
                profileDataSource.close();

                // Call to activate new Profile.
                ProfileActivator profileActivator = new ProfileActivator();
                profileActivator.activateProfile(context, profile);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    public void loadProfileList()
    {
        // Get User defined User profile Names.
        UserProfileDataSource userProfileDataSources = new UserProfileDataSource(context);
        userProfileDataSources.open();
        String[] profileNames = userProfileDataSources.getAllProfileNames();
        userProfileDataSources.close();

        // Set user profiles to List View
        profileList = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,
                android.R.id.text1, profileNames) ;
        listView.setAdapter(profileList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }




}
