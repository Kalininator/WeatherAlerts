package uk.kalinin.weatheralerts;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by kal on 18/12/2017.
 */
@Entity(tableName = "weather_events")
public class WeatherEvent {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "event_name")
    private String eventName;


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
