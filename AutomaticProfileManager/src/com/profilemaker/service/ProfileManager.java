package com.profilemaker.service;

import android.app.Application;
import android.content.Context;

public class ProfileManager extends Application
{
    private static ProfileManager instance = null;

    public ProfileManager()
    {
        instance = this;
    }
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        
        
    }

    public static Context getContext()
    {
        if (instance == null) instance = new ProfileManager();

        return instance;
    }
}

