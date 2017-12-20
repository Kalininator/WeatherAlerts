package uk.kalinin.weatheralerts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kal on 20/12/2017.
 */

public class WeatherRequest {

    private Context mContext;
    private RequestQueue queue;

    private static String baseUrl = "https://api.darksky.net/forecast/36648ca6a6cf34071625034350e03e63/LAT,LON,DATETIME?exclude=minutely,hourly,daily&units=si";

    public WeatherRequest(Context ctx){
        mContext = ctx;

        queue = Volley.newRequestQueue(mContext);
    }

    public void getWeather(int eventID){

        //get event

        final WeatherEvent event = AppDatabase.getDao(mContext).getById(eventID);
        if(event != null){
            //get event details
            String url = baseUrl;

            //replace lat/long in url
            url = url.replace("LAT",Double.toString(event.getLat()));
            url = url.replace("LON",Double.toString(event.getLon()));

            //replace date/time in url
            long millis = event.getDatetime();
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            Date date = new Date(millis);
            String datetime = sdfDate.format(date) + "T" + sdfTime.format(date) + ":00";
            url = url.replace("DATETIME",datetime);

            //Log.d("kalcat",url); checked and it works
            //make request https://developer.android.com/training/volley/simple.html#simple
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Response acquired

                            try {
                                JSONObject json = new JSONObject(response);
                                JSONObject currently = json.getJSONObject("currently");
                                final String result = currently.getString("icon");
                                final double temperature = currently.getDouble("temperature");

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String weather = WeatherRequest.convertWeatherIconResult(result);
                                        Log.d("kalcat",event.getId() + ": " + weather);

                                        String prevWeather = AppDatabase.getDao(mContext).getById(event.getId()).getPredictedWeather();
                                        Double prevTemp = AppDatabase.getDao(mContext).getById(event.getId()).getTemperature();
                                        SharedPreferences settings = mContext.getSharedPreferences("settings",0);
                                        boolean tempNotify = settings.getBoolean("tempNotify",false);
                                        if(weather != prevWeather || ((prevTemp != temperature) && tempNotify)){
                                            //weather report changed
                                            //create notification

                                            NotificationCompat.Builder mBuilder =
                                                    new NotificationCompat.Builder(mContext)
                                                            .setSmallIcon(R.drawable.ic_stat_name)
                                                    .setContentTitle("Weather Report: " + event.getEventName())
                                                    .setContentText(weather);
                                            Intent resultIntent = new Intent(mContext,MainActivity.class);
                                            //create on click for notification intent
                                            PendingIntent resultPendingIntent =
                                                    PendingIntent.getActivity(
                                                            mContext,
                                                            0,
                                                            resultIntent,
                                                            PendingIntent.FLAG_UPDATE_CURRENT
                                                    );
                                            mBuilder.setContentIntent(resultPendingIntent);
                                            NotificationManager mNotifyMgr =
                                                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                            mNotifyMgr.notify(event.getId(), mBuilder.build());
                                        }

                                        AppDatabase.getDao(mContext).updateWeather(event.getId(),weather);
                                        AppDatabase.getDao(mContext).updateTemperature(event.getId(),temperature);
                                    }
                                }).start();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("kalcat","error response");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }else{
            Log.d("kalcat","Event not in db");
        }

    }

    public void getWeatherFromDetails(double lat, double lon, long millis,Response.Listener<String> listener ){
        String url = baseUrl;

        //replace lat/long in url
        url = url.replace("LAT",Double.toString(lat));
        url = url.replace("LON",Double.toString(lon));

        //replace date/time in url
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Date date = new Date(millis);
        String datetime = sdfDate.format(date) + "T" + sdfTime.format(date) + ":00";
        url = url.replace("DATETIME",datetime);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("kalcat","error response");
            }
        });

        // Add the request to the RequestQueue.
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    public void updateAllWeatherPredictions(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get all events
                List<WeatherEvent> events = AppDatabase.getDao(mContext).getAll();

                for(WeatherEvent event : events){
                    getWeather(event.getId());
                }
                Log.d("kalcat","queued all requests");
            }
        }).start();
    }


    public static String convertWeatherIconResult(String result){
        String out = "Unknown";
        switch (result){
            case "wind":
            case "clear-day":
            case "clear-night":
                out = "Clear";
                break;
            case "partly-cloudy-day":
            case "partly-cloudy-night":
            case "cloudy":
                out = "Cloudy";
                break;
            default:
                out = result;
                break;
        }
        return out;
    }

}
