package com.mdm.activities;


import android.app.Activity;
import android.os.Bundle;
import com.emanuelef.remote_capture.Utils;
import com.emanuelef.remote_capture.activities.BaseActivity;
import com.emanuelef.remote_capture.activities.PasswordManager;
/**
 * Activity שמציג את ה-Fragment של ההגדרות.
 */
public class storeSettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("הגדרות");
        displayBackAction();
        // טען את ה-Fragment של ההגדרות
        PasswordManager.requestPasswordAndSave(new Runnable(){
                @Override
                public void run() {
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new storeSettingsFragment())
            .commit();
                }
            }, this);
    }
}
