package uk.kalinin.weatheralerts;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by kal on 18/12/2017.
 */
@Entity(tableName = "weather_events")
public class WeatherEvent {
    @PrimaryKey(autoGenerate = true)
    private int id;

    //Name of event
    @ColumnInfo(name = "event_name")
    private String eventName;

    //Last predicted weather
    //helps app know if its changed
    @ColumnInfo(name = "predicted_weather")
    private String predictedWeather;

    //location latitude
    @ColumnInfo(name = "lat")
    private double lat;

    //Location Longitude
    @ColumnInfo(name = "lon")
    private double lon;

    //DateTime of event
    @ColumnInfo(name = "date_time")
    private long datetime;

    @ColumnInfo(name = "temperature")
    private double temperature;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getPredictedWeather() {
        return predictedWeather;
    }

    public void setPredictedWeather(String predictedWeather) {
        this.predictedWeather = predictedWeather;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
