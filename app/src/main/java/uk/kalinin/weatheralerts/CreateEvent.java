package uk.kalinin.weatheralerts;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

public class CreateEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TextView txt_Date, txt_Weather, txt_Time, txt_Location;

    int year, month, day, hour, minute;//store date/time info before converting to millis

    //booleans that track whether prerequisites for creation are complete
    boolean dateSet = false;
    boolean timeSet = false;
    boolean locationSet = false;
    boolean weatherSet = false;

    //Store initial weather and temperature values
    String weather = "not init";
    double temperature;

    //store location
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Set toolbar title
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_createEvent);
        myToolbar.setTitle("Create Event");
        setSupportActionBar(myToolbar);

        txt_Date = (TextView) findViewById(R.id.txt_Date);
        txt_Time = (TextView) findViewById(R.id.txt_Time);
        txt_Location = (TextView) findViewById(R.id.txt_Location);
        txt_Weather = (TextView) findViewById(R.id.txt_Weather);
    }

    public void onSetLocation(View view) {
        //check location permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //permission not granted
            //request permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //permission granted
            setLocation();
        }

    }

    private long calculateMillis(){
        //Make a calendar with known values and calcuate milliseconds
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minute);
        return c.getTimeInMillis();
    }

    //needed for creation process, as event object is not yet produced when this is needed
    public void getWeatherOnClick(View view){

        boolean allRequirements = true;

        long millis = 0;

        //Create long form date/time
        if(dateSet && timeSet){
            //convert to milliseconds for storing
            millis = calculateMillis();
        }else{
            allRequirements = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Date and Time need to be set",Toast.LENGTH_SHORT).show();
                }
            });
        }

        //check if location is set
        if(!locationSet){
            allRequirements = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Location needs to be set",Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(allRequirements){//ready to get initial weather reading
            //Create new weather request object
            WeatherRequest req = new WeatherRequest(getApplicationContext());
            //listener to parse json response and set weather/temp on success
            Response.Listener<String> listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Response acquired
                    try {
                        final String response_f = response;
                        //convert to JSON object
                        JSONObject json = new JSONObject(response);
                        final JSONObject currently = json.getJSONObject("currently");
                        final String result = currently.getString("icon");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("kalcat","got weather: " + result);
                                //Categorise possible weather results
                                weather = WeatherRequest.convertWeatherIconResult(result);
                                try {
                                    temperature = currently.getDouble("temperature");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    temperature = -100;
                                    //-100 represents unknown
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(temperature == -100){
                                            //temperature unknown
                                            txt_Weather.setText(weather);
                                        }else{
                                            txt_Weather.setText(weather + " " + temperature + "\u00b0C");
                                        }
                                    }
                                });
                                weatherSet = true;
                                //Dont need to cache as its not for a created event

                            }
                        }).start();

                    } catch (JSONException e) {
                        Log.e("JSON parsing error",e.toString());
                    }
                }
            };
            Response.ErrorListener errorListener =  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Weather Response","no response");
                    //Dont need to cache as its not for a created event
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }
            };
            //request weather information
            req.getWeatherFromDetails(lat,lon,millis,listener,errorListener);
        }
    }


    private void setLocation() {
        try{
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = locationManager.GPS_PROVIDER;
            Location lastKnown = locationManager.getLastKnownLocation(locationProvider);
            if (lastKnown == null) {
                lat = 0;
                lon = 0;
            }else{
                lat = lastKnown.getLatitude();
                lon = lastKnown.getLongitude();
            }
            //display location
            txt_Location.setText(lat + "," + lon);
            locationSet = true;

        }catch(SecurityException c){
            //User did not give permission for location
            //Shouldnt occur, as this function only gets called if permissions are obtained
            Toast.makeText(getApplicationContext(),"Location permissions required",Toast.LENGTH_SHORT).show();
        }

    }



    //https://developer.android.com/training/permissions/requesting.html
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    setLocation();
                } else {

                    // permission denied
                    Toast.makeText(getApplicationContext(),"Need location permission to get location",Toast.LENGTH_SHORT);
                }
                return;
            }
        }
    }

    public void onCreateClick(View view){

        new Thread(new Runnable() {
            @Override
            public void run() {

                EditText inpName = (EditText)findViewById(R.id.inp_name);



                boolean allRequirements = true;

                //Get event name
                String eventName = inpName.getText().toString();


                if(eventName.length() == 0){//Event Name cant be blank
                    allRequirements = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Event Name Can't Be Empty",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                long millis = 0;

                //Create long form date/time
                //ensure date and time are set
                if(dateSet && timeSet){
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR,year);
                    c.set(Calendar.MONTH,month);
                    c.set(Calendar.DAY_OF_MONTH,day);
                    c.set(Calendar.HOUR_OF_DAY,hour);
                    c.set(Calendar.MINUTE,minute);
                    millis = c.getTimeInMillis();
                }else{
                    allRequirements = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Date and Time need to be set",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //check if location is set
                if(!locationSet){
                    allRequirements = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Location needs to be set",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //check if weather is set
                if(!weatherSet){
                    allRequirements = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Weather needs to be set",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //all requirements met
                if(allRequirements){//insert into new object and add to db
                    WeatherEventDao dao = AppDatabase.getAppDatabase(getApplicationContext()).weatherEventDao();
                    WeatherEvent i = new WeatherEvent();
                    i.setEventName(eventName);
                    i.setDatetime(millis);
                    i.setLat(lat);
                    i.setLon(lon);
                    i.setPredictedWeather(weather);
                    i.setTemperature(temperature);
                    dao.insertAll(i);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Added Event",Toast.LENGTH_SHORT).show();
                        }
                    });


                    finish();
                }

            }
        }).start();

    }

    public void startDatePicker(View view){
        DatePickerFragment frag = new DatePickerFragment();
        frag.show(getSupportFragmentManager(),"Date");
    }

    public void startTimePicker(View view){
        TimePickerFragment frag = new TimePickerFragment();
        frag.show(getSupportFragmentManager(),"Time");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        //display time formatted in textbox
        txt_Time.setText(String.format("%02d:%02d",hour,minute));
        timeSet = true;
    }


    public static class TimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),
                    (TimePickerDialog.OnTimeSetListener)getActivity(),
                    hour,minute,true);
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        txt_Date.setText(this.day + "/" + this.month + "/" + this.year);
        dateSet = true;
    }


    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener)
                            getActivity(), year, month, day);
        }
    }
}
