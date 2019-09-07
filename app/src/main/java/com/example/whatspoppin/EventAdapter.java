package com.example.whatspoppin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.whatspoppin.ui.eventlist.Event;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {
    private ArrayList<Event> eventList = new ArrayList<Event>();
    private Context context;

    public EventAdapter(Context context) {
        this.context = context;

        Event eventA = new Event("EventA", "DateA");
        Event eventB = new Event("EventB", "DateB");
        Event eventC = new Event("EventC", "DateC");
        eventList.add(eventA);
        eventList.add(eventB);
        eventList.add(eventC);
        //CommitteeDatabaseAdapter dbAdapter = new CommitteeDatabaseAdapter(context);
        //committeeList = dbAdapter.readAllCommittees();
    }

    /*public void updateDataSet() {
        CommitteeDatabaseAdapter dbAdapter = new CommitteeDatabaseAdapter(context);
        committeeList = dbAdapter.readAllCommittees();
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Event event = eventList.get(position);

        View v = vi.inflate(R.layout.eventlist_item, null);
        TextView eventName = (TextView) v.findViewById(R.id.text_eventName);

        eventName.setText(event.getEventName());
        return v;
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
