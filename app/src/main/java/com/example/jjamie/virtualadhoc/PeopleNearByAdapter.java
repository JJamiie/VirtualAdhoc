package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by JJamie on 2/7/16 AD.
 */
public class PeopleNearByAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater mInflater;
    private ArrayList<Neighbor> neighbors;

    public PeopleNearByAdapter(Activity activity) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        neighbors = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return neighbors.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_listnetwork, null);
            holder.imagePeople = (ImageView) convertView.findViewById(R.id.item_list_network_picture_network);
            holder.textPeopleNearBy = (TextView) convertView.findViewById(R.id.item_list_network_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(activity).load(R.drawable.profile).bitmapTransform(new CropCircleTransformation(activity)).into(holder.imagePeople);
        //Set neighbor
        String senderName = neighbors.get(position).senderName;
        holder.textPeopleNearBy.setText(senderName);

        return convertView;
    }

    public class ViewHolder {
        ImageView imagePeople;
        TextView textPeopleNearBy;
    }

    public ArrayList<Neighbor> addNeighbors(Neighbor neighbor) {
        //Check neighbor is exist in neighbors
        for(int i =0;i<neighbors.size();i++){
            if(neighbors.get(i).senderName.equals(neighbor.senderName)){
                return new ArrayList<Neighbor>();
            }
        }
        neighbors.add(neighbor);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        return neighbors;

    }

    public void setNeighbors(ArrayList<Neighbor> neighbors) {
        this.neighbors = neighbors;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
    public void clearNeighbor(){
        neighbors.clear();
    }


}
