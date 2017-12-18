package uk.kalinin.weatheralerts;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by kal on 18/12/2017.
 */

@Database(entities = {WeatherEvent.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract WeatherEventDao  weatherEventDao();

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,AppDatabase.class,"weather_event_database").build();
        }
        return INSTANCE;
    }
}
