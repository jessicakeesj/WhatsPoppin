package com.example.whatspoppin.ui.eventlist;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.whatspoppin.Event;
import com.example.whatspoppin.R;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Event> eventList = new ArrayList<Event>();
    private ArrayList<Event> filteredList = new ArrayList<Event>();
    private Context context;
    private static LayoutInflater inflater = null;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EventAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        this.filteredList = new ArrayList<Event>();
        this.filteredList.addAll(eventList);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = filteredList.get(position);
        View v = convertView;
        if (convertView == null)v = inflater.inflate(R.layout.eventlist_item, null);
        TextView eventName = (TextView) v.findViewById(R.id.text_eventName);
        String dateString = formatDate(event.getEvent_datetime_start());
        String sourceString;
        if(event.getEventLocationSummary() == null || event.getEventLocationSummary() == "null"){
            sourceString = "<b>" + event.getEventName() + "</b> " + "<br>" + dateString;
        }else{
            sourceString = "<b>" + event.getEventName() + "</b> " + "<br>" + dateString + "<br>" + event.getEventLocationSummary();
        }
        eventName.setText(Html.fromHtml(sourceString));
        return v;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Event getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new FilterResults();
                String charString = charSequence.toString();
                //If there's nothing to filter on, return the original data to list
                if(charSequence == null || charSequence.length() == 0)
                {
                    results.values = eventList;
                    results.count = eventList.size();
                } else
                {
                    ArrayList<Event> filterResultsData = new ArrayList<Event>();

                    for(Event e : eventList)
                    {
                        if(e.getEventName().toLowerCase().contains(charString.toLowerCase()))
                        {
                            filterResultsData.add(e);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                filteredList = (ArrayList<Event>)filterResults.values;
                notifyDataSetChanged();
            }
        };
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
}
