package uk.kalinin.weatheralerts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView = (RecyclerView) findViewById(R.id.eventsList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{25.0f, 25.0f}, 0));
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).paint(paint).build());

        new Thread(new Runnable() {//Run in new thread because it might make delays
            @Override
            public void run() {
                WeatherEventDao dao = AppDatabase.getAppDatabase(getApplicationContext()).weatherEventDao();
                List<WeatherEvent> myDataSet = dao.getAll();
                mAdapter = new EventAdapter(myDataSet);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });

            }
        }).start();

        Intent i = new Intent(getApplicationContext(),WeatherUpdateService.class);
        getApplicationContext().startService(i);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),0,i,0);

        Calendar c = Calendar.getInstance();
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,pendingIntent);



    }

    public void openSettings(View view){
        Intent i = new Intent(this,Settings.class);
        startActivity(i);
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get new list of events
                WeatherEventDao dao = AppDatabase.getAppDatabase(getApplicationContext()).weatherEventDao();
                final List<WeatherEvent> myDataSet = dao.getAll();
                runOnUiThread(new Runnable() {
                    //run on ui thread because same thread needed for changing views
                    @Override
                    public void run() {
                        mAdapter.updateEvents(myDataSet);
                        Log.d("kalcat","event list updated");
                    }
                });
            }
        }).start();
    }

    public void btn_AddEvent(View view){
        Intent intent = new Intent(this,CreateEvent.class);
        startActivity(intent);
    }


}
