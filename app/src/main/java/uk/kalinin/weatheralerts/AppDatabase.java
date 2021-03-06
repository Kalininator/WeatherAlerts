package uk.kalinin.weatheralerts;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by kal on 18/12/2017.
 */

@Database(entities = {WeatherEvent.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {

    //Singleton pattern to ensure the same database instance is used

    private static AppDatabase INSTANCE;

    public abstract WeatherEventDao  weatherEventDao();

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,AppDatabase.class,"weather_event_database").build();
        }
        return INSTANCE;
    }

    //Cleaner way to access Dao
    public static WeatherEventDao getDao(Context context){
        return getAppDatabase(context).weatherEventDao();
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }
}
