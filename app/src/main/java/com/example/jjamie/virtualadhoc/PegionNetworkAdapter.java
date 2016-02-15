package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by JJamie on 2/7/16 AD.
 */
public class PegionNetworkAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private static ArrayList<String> listNeighbor;
    private Activity activity;
    private Boolean enabled_network = false;
    private TextView txt_scanning_Network;
    private TextView txt_pigeon_network;
    private ListView list_people_nearby;
    private ListView list_pigeon_network;
    private TextView txt_manage_network;
    public static Boolean leaveNetwork = false;

    public PegionNetworkAdapter(Activity activity, View view) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        txt_scanning_Network = (TextView) view.findViewById(R.id.txt_scanningNetwork);
        txt_manage_network = (TextView) view.findViewById(R.id.txt_mange_network);
        txt_pigeon_network = (TextView) view.findViewById(R.id.pigeon_network_text);
        list_people_nearby = (ListView) view.findViewById(R.id.list_people_nearby);
        list_pigeon_network = (ListView) view.findViewById(R.id.list_pigeon_network);
        listNeighbor = new ArrayList<>();
    }

    @Override
    public int getCount() {
        if (!enabled_network) return 0;
        return listNeighbor.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        holder = new ViewHolder();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_listnetwork, null);
            holder.picture_network = (ImageView) convertView.findViewById(R.id.item_list_network_picture_network);
            holder.name_network = (TextView) convertView.findViewById(R.id.item_list_network_name);
            holder.btn_join_network = (Button) convertView.findViewById(R.id.btn_join_network);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //Set picture network
        Glide.with(activity).load(R.drawable.profile).bitmapTransform(new CropCircleTransformation(activity)).into(holder.picture_network);
        //Set neighbor
        final String SSID = listNeighbor.get(position);
        holder.name_network.setText(SSID);

        holder.btn_join_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage("Are you want to join " + SSID + " ?");
                dialog.setCancelable(true);

                dialog.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                setEnabled_network(false);
                                ConnectionManager.clientJoinAp(SSID, activity);
                                txt_pigeon_network.setText("People nearby");
                                txt_manage_network.setText("Leave network");
                                list_people_nearby.setVisibility(View.VISIBLE);
                                list_pigeon_network.setVisibility(View.INVISIBLE);
                                //Wait for time to connect Hotspot
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            Thread.sleep(5000);
                                            ReportNeighbor.clientReportJoin();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                leaveNetwork = true;
                            }
                        });

                dialog.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dialog.show();
            }
        });
        return convertView;
    }

    public void getListNeighbor() {
        listNeighbor = ConnectionManager.listNeighbourAp(activity);
        setTxtScanningInvisibility(View.VISIBLE);
        System.out.println("update scanning..........");
    }


    public void setEnabled_network(Boolean is_enable_network) {
        this.enabled_network = is_enable_network;
        if (is_enable_network) {
            scanning();
        } else {
            if (th_scanning != null) {
                th_scanning.interrupt();
            }
            setTxtScanningInvisibility(View.INVISIBLE);
        }
    }

    public Boolean getEnabled_network() {
        return enabled_network;
    }

    private Thread th_scanning = null;

    public void scanning() {
        th_scanning = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    getListNeighbor();
                    try {
                        if (!getEnabled_network()) {
                            break;
                        }
                        Thread.sleep(2000);
                        setTxtScanningInvisibility(View.INVISIBLE);
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setTxtScanningInvisibility(View.INVISIBLE);
            }
        };
        th_scanning.start();
    }

    public void setTxtScanningInvisibility(final int visible) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_scanning_Network.setVisibility(visible);
            }
        });
        notifyDataSetChange();

    }

    public void notifyDataSetChange() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public class ViewHolder {
        ImageView picture_network;
        TextView name_network;
        Button btn_join_network;

    }

    public static void clearlistNeighbor() {
        listNeighbor.clear();
        System.out.println("Clear: " + listNeighbor.size());
    }


}


