package com.stav.ideastreet.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stav.ideastreet.R;
import com.stav.ideastreet.utils.UIUtils;

public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void stav(View v) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
