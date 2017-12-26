package uk.kalinin.weatheralerts;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by kal on 20/12/2017.
 */

public class WeatherUpdateService extends IntentService {
    public WeatherUpdateService() {
        super("WeatherEvent Update Service");
    }


    //create update request for all events
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        WeatherRequest req = new WeatherRequest(getApplicationContext());
        req.updateAllWeatherPredictions();
    }
}
