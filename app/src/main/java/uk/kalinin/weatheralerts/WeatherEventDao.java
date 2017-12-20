package uk.kalinin.weatheralerts;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by kal on 18/12/2017.
 */

@Dao
public interface WeatherEventDao {

    @Query("SELECT * FROM weather_events")
    List<WeatherEvent> getAll();

    @Query("SELECT * FROM weather_events WHERE id = :id")
    WeatherEvent getById(int id);

    @Query("SELECT COUNT(*) FROM weather_events")
    int getCount();

    @Insert
    void insertAll(WeatherEvent... events);

    @Delete
    void delete(WeatherEvent weatherEvent);

    @Query("UPDATE weather_events SET predicted_weather = :weather WHERE id = :id")
    void updateWeather(int id,String weather);

    @Query("UPDATE weather_events SET temperature = :temp WHERE id = :id")
    void updateTemperature(int id,double temp);
}
