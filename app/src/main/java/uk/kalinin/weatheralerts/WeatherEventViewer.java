package uk.kalinin.weatheralerts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeatherEventViewer extends AppCompatActivity {

    private TextView txt_Name,txt_DateTime,txt_Weather,txt_Location;
    private WeatherEvent event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_event_viewer);
        txt_Name = (TextView) findViewById(R.id.txt_Name);
        txt_DateTime = (TextView) findViewById(R.id.txt_DateTime);
        txt_Location = (TextView) findViewById(R.id.txt_Location);
        txt_Weather = (TextView) findViewById(R.id.txt_Weather);

        //get primary key
        Intent intent = getIntent();
        final int key = intent.getIntExtra("PrimaryKey",-1);
        if(key!=-1){//if a key was passed
            new Thread(new Runnable() {
                @Override
                public void run() {
                    event = AppDatabase.getDao(getApplicationContext()).getById(key);
                    updateFields();
                }
            }).start();
        }

    }

    public void saveToHTML(View view){

        //check location permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //permission not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            //permission granted
            saveHTML();
        }
    }

    private void saveHTML(){
        Date d = new Date(event.getDatetime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String contents = "<html><body>" +
                "<h1>" + event.getEventName() + "</h1>" +
                "<p>Date/Time: " + sdf.format(d) + "</p>" +
                "<p>Location: " + event.getLat() + "," + event.getLon() + "</p>" +
                "<p>Weather Expected: " + event.getPredictedWeather()  + " " + event.getTemperature() + "\u00b0C" + "</p>" +
                "</body></html>";
        File dir = new File(Environment.getExternalStorageDirectory() + "/WeatherEvents");
        dir.mkdirs();
        File file = new File(dir + "/" + event.getEventName() + ".html");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(contents.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    saveHTML();
                } else {

                    // permission denied
                    Toast.makeText(getApplicationContext(),"Need write permissions to save file",Toast.LENGTH_SHORT);
                }
                return;
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void addToCalendar(View view){

        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setType("vnd.android.cursor.item/event");
        i.putExtra("beginTime",event.getDatetime());
        i.putExtra("allDay",true);
        i.putExtra("title", event.getEventName());
        startActivity(i);
    }

    public void onViewLocationClicked(View view){
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f",event.getLat(), event.getLon());
        Intent intent_map = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent_map);
    }

    private void updateFields(){
        if(event!= null){
            txt_Name.setText(event.getEventName());
            txt_Location.setText(event.getLat() + "," + event.getLon());
            txt_Weather.setText(event.getPredictedWeather() + " " + event.getTemperature() + "\u00b0C");
            Date d = new Date(event.getDatetime());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            txt_DateTime.setText(sdf.format(d));
        }
    }

    public void deleteOnClick(View view){
        //try delete this event
        if(event!=null){//if a key was passed
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //event = AppDatabase.getDao(getApplicationContext()).getById(key);
                    AppDatabase.getDao(getApplicationContext()).delete(event);
                    //updateFields();
                    //close activity
                    finish();
                }
            }).start();
        }
    }
}
