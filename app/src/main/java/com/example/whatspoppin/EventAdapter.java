package com.example.whatspoppin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventAdapter extends BaseAdapter {
    private ArrayList<Event> eventList = new ArrayList<Event>();
    private Context context;
    private EventAdapter eventAdapter;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("events");

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EventAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Event event = eventList.get(position);

        View v = vi.inflate(R.layout.eventlist_item, null);
        TextView eventName = (TextView) v.findViewById(R.id.text_eventName);

        String dateString = formatDate(event.getEvent_datetime_start());

        eventName.setText(event.getEventName() + "\n" + dateString);
        return v;
    }

    private String formatDate(String eventDate){
        String dateString = null;

        String[] weekdays = new String[7];
        weekdays[0] = "Sunday";
        weekdays[1] = "Monday";
        weekdays[2] = "Tuesday";
        weekdays[3] = "Wednesday";
        weekdays[4] = "Thursday";
        weekdays[5] = "Friday";
        weekdays[6] = "Saturday";

        String[] months = new String[12];
        months[0] = "January";
        months[1] = "Feburary";
        months[2] = "March";
        months[3] = "April";
        months[4] = "May";
        months[5] = "June";
        months[6] = "July";
        months[7] = "August";
        months[8] = "September";
        months[9] = "October";
        months[10] = "November";
        months[11] = "December";

        try {
            Date date = simpleDateFormat.parse(eventDate);
            int weekday = date.getDay();
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear() + 1900;
            dateString = day + " " + months[month] + " " + year + " (" + weekdays[weekday] +")";
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Event getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
