package com.example.whatspoppin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecommendAdapter extends BaseAdapter {
    private ArrayList<Event> recommendList = new ArrayList<Event>();
    private Context context;

    public RecommendAdapter(Context context) {
        this.context = context;

        Event eventC = new Event("EventC", "DateC");
        Event eventD = new Event("EventD", "DateD");
        recommendList.add(eventC);
        recommendList.add(eventD);
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

        Event event = recommendList.get(position);

        View v = vi.inflate(R.layout.recommendlist_item, null);
        TextView eventName = (TextView) v.findViewById(R.id.text_recommendName);

        eventName.setText(event.getEventName());
        return v;
    }

    @Override
    public int getCount() {
        return recommendList.size();
    }

    @Override
    public Event getItem(int position) {
        return recommendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}