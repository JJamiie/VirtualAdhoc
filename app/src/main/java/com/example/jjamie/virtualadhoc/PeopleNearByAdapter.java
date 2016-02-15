package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by JJamie on 2/7/16 AD.
 */
public class PeopleNearByAdapter extends BaseAdapter {
    private ListView list_pigeon_network;
    private ListView list_people_nearby;
    private Activity activity;
    private LayoutInflater mInflater;
    private ArrayList<Neighbor> neighbors;
    private TextView txt_manage_network;
    private RelativeLayout tab_header_pigeon_network;


    public PeopleNearByAdapter(Activity activity,View view) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        neighbors = new ArrayList<>();
        tab_header_pigeon_network = (RelativeLayout) view.findViewById(R.id.tab_header_pigeon_network);
        list_people_nearby = (ListView) view.findViewById(R.id.list_people_nearby);
        list_pigeon_network = (ListView) view.findViewById(R.id.list_pigeon_network);
        txt_manage_network = (TextView) view.findViewById(R.id.txt_mange_network);

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
        //Check if neighbor is exist in neighbors, pass it.
        for(int i =0;i<neighbors.size();i++){
            if(neighbors.get(i).senderName.equals(neighbor.senderName)){
                return neighbors;
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

    public ArrayList<Neighbor> removeNeighbors(Neighbor neighbor) {
        //Check if neighbor is exist in neighbors,remove it.
        int postionToRemove = -1;
        for(int i =0;i<neighbors.size();i++){
            if(neighbors.get(i).senderName.equals(neighbor.senderName)){
                postionToRemove = i;
            }
        }

        if(postionToRemove != -1) {
            neighbors.remove(postionToRemove);
        }
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }


    public void turnManangeNetworkOff() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tab_header_pigeon_network.setVisibility(View.INVISIBLE);
                list_people_nearby.setVisibility(View.GONE);
                list_pigeon_network.setVisibility(View.GONE);
                txt_manage_network.setText("Find network");
                MateFragment.is_btn_manage_network_click = false;

            }
        });
        PegionNetworkAdapter.clearlistNeighbor();
    }
}
