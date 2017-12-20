package uk.kalinin.weatheralerts;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    Switch sw_tempNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sw_tempNotify = (Switch)findViewById(R.id.switch_tempnotify);

        SharedPreferences settings = getSharedPreferences("settings",0);
        boolean tempNotify = settings.getBoolean("tempNotify",false);

        sw_tempNotify.setChecked(tempNotify);

        sw_tempNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences settings = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("tempNotify", b);
                editor.commit();
            }
        });
    }

}
