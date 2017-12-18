package uk.kalinin.weatheralerts;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by kal on 18/12/2017.
 */

@Dao
public interface WeatherEventDao {

    @Query("SELECT * FROM weather_events")
    List<WeatherEvent> getAll();

    @Insert
    void insertAll(WeatherEvent... events);

    @Delete
    void delete(WeatherEvent weatherEvent);
}
