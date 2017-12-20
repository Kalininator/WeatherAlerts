package uk.kalinin.weatheralerts;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kal on 18/12/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {


    private List<WeatherEvent> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameTextView;
        private TextView mDateTimeTextView;
        private TextView mWeatherTextView;

        private WeatherEvent event;

        public ViewHolder(ConstraintLayout tv) {
            super(tv);
            mNameTextView = (TextView) tv.findViewById(R.id.txt_Name);
            mDateTimeTextView = (TextView) tv.findViewById(R.id.txt_DateTime);
            mWeatherTextView = (TextView) tv.findViewById(R.id.txt_Weather);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d("kalcat","onclick");
                    if(event != null){
                        Intent i = new Intent(view.getContext(),WeatherEventViewer.class);
                        //pass id of event, as its a primary key
                        i.putExtra("PrimaryKey",event.getId());
                        view.getContext().startActivity(i);
                    }
                }
            });
        }

        public void setEvent(WeatherEvent event) {
            this.event = event;
            mNameTextView.setText(event.getEventName());
            //set date time in view
            long millis = event.getDatetime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = new Date(millis);
            mDateTimeTextView.setText(sdf.format(date));
            mWeatherTextView.setText(event.getPredictedWeather() + " " + event.getTemperature() + "\u00b0C");
            Log.d("kalcat","displayed weather " + event.getPredictedWeather());
        }
    }

    public EventAdapter(List<WeatherEvent> myDataSet){
        mDataSet = myDataSet;
    }

    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ConstraintLayout v = (ConstraintLayout) inflater.inflate(R.layout.event_list_view,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, final int position) {
//        holder.setNameText(mDataSet.get(position).getEventName());
        holder.setEvent(mDataSet.get(position));
    }

    public void updateEvents(List<WeatherEvent> newList){
        mDataSet.clear();
        mDataSet.addAll(newList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
