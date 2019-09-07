package com.example.whatspoppin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookmarkAdapter extends BaseAdapter {
    private ArrayList<Event> bookmarkList = new ArrayList<Event>();
    private Context context;

    public BookmarkAdapter(Context context) {
        this.context = context;

        Event eventB = new Event("EventB", "DateB");
        Event eventC = new Event("EventC", "DateC");
        Event eventD = new Event("EventD", "DateD");
        bookmarkList.add(eventB);
        bookmarkList.add(eventC);
        bookmarkList.add(eventD);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);
        bookmarkList.add(eventB);

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

        Event event = bookmarkList.get(position);

        View v = vi.inflate(R.layout.bookmarklist_item, null);
        TextView eventName = (TextView) v.findViewById(R.id.text_bookmarkName);

        eventName.setText(event.getEventName());
        return v;
    }

    @Override
    public int getCount() {
        return bookmarkList.size();
    }

    @Override
    public Event getItem(int position) {
        return bookmarkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
