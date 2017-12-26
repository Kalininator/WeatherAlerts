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


    //Converts event object into a RecyclerView item for displaying

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

            //set onclick for whole item to display more details
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(event != null){
                        Intent i = new Intent(view.getContext(),WeatherEventViewer.class);
                        //pass id of event to event viewer class
                        i.putExtra("PrimaryKey",event.getId());
                        view.getContext().startActivity(i);
                    }
                }
            });
        }

        public void setEvent(WeatherEvent event) {
            //once an event is set, detals can be displayed
            this.event = event;
            mNameTextView.setText(event.getEventName());
            //set date time in view
            long millis = event.getDatetime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = new Date(millis);
            mDateTimeTextView.setText(sdf.format(date));
            mWeatherTextView.setText(event.getPredictedWeather() + " " + event.getTemperature() + "\u00b0C");
        }
    }

    public EventAdapter(List<WeatherEvent> myDataSet){
        mDataSet = myDataSet;
    }

    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //inflate xml layout file
        ConstraintLayout v = (ConstraintLayout) inflater.inflate(R.layout.event_list_view,parent,false);
        //pass view to view holder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, final int position) {
        holder.setEvent(mDataSet.get(position));
        //set event for each view as its binded
    }

    public void updateEvents(List<WeatherEvent> newList){
        //force refresh of details in views
        mDataSet.clear();
        mDataSet.addAll(newList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
