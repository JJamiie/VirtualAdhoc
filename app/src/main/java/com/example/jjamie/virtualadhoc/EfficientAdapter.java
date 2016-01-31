package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by JJamie on 1/2/16 AD.
 */
public class EfficientAdapter extends BaseAdapter {
    public Context mContext;
    public LayoutInflater mInflater;
    public Activity activity;
    public static EfficientAdapter adapter;
    private static final int TYPE_MAX_COUNT = 2;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FIRST = 1;
    private ConnectionManager connectionManager;

    public EfficientAdapter(Activity activity) {
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
        mInflater = LayoutInflater.from(mContext);
        adapter = this;


    }

    @Override
    public int getCount() {
        if (ManageImage.getFile() == null) return 0;
        return ManageImage.getFile().length + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_FIRST : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    int sequenceNumber = 0;
    boolean isStartPegion = false;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        holder = new ViewHolder();
        switch (type) {
            case TYPE_ITEM:
                if (convertView == null || convertView.getTag() == null) {
                    convertView = mInflater.inflate(R.layout.item_listview, null);
                    holder.title = (TextView) convertView.findViewById(R.id.item_listview_name);
                    holder.picture_profile = (ImageView) convertView.findViewById(R.id.item_listview_profile_picture);
                    holder.picture_show = (ImageView) convertView.findViewById(R.id.item_picture);
                    holder.description = (TextView) convertView.findViewById(R.id.item_listview_description);
                    holder.sent = (Button) convertView.findViewById(R.id.sent);
                    holder.show_gps_map = (ImageView) convertView.findViewById(R.id.show_gps_map);
                    convertView.setTag(holder); //deposit to tag
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                String contact = "";
                String caption = "";
                try {
                    com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(ManageImage.getFile()[position - 1]);
                    for (Directory directory : metadata.getDirectories()) {
                        for (com.drew.metadata.Tag tag : directory.getTags()) {
                            if (tag.getTagName().equals("Contact")) {
                                contact = tag.getDescription();
                            } else if (tag.getTagName().equals("Keywords")) {
                                caption = tag.getDescription();
                            } else if (tag.getTagName().equals("Sub-location")) {
                                if (!tag.getDescription().equals("null")) {
                                    holder.show_gps_map.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                } catch (ImageProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Set sender's name
                final String senderName = contact;
                holder.title.setText(senderName);
                //Set description
                final String description = contact;
                holder.description.setText(caption);
                //Set picture profile
                Glide.with(mContext).load(R.drawable.profile).bitmapTransform(new CropCircleTransformation(mContext)).into(holder.picture_profile);
                //Set image in list
                final File fileImage = new File(ManageImage.getFile()[position - 1].getPath());
                Glide.with(mContext).load(fileImage).centerCrop().placeholder(new ColorDrawable(0xFFc5c4c4)).into(holder.picture_show);


                //Set sent button
                holder.sent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("sentttttttttttttttttt");

                        try {
                            byte[] img = new byte[(int) fileImage.length()];
                            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileImage));
                            buf.read(img, 0, img.length);
                            buf.close();
                            Image image = new Image(senderName, sequenceNumber, img);
                            sequenceNumber++;
                            Broadcaster.broadcast(image);

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "sent", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (IOException ex) {

                        } catch (SenderNameIncorrectLengthException ex) {

                        }
                    }
                });

                //Set gps button
                holder.show_gps_map.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });


                break;
            case TYPE_FIRST:
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_touchtopegion, null);
                }
                final ImageView circleTurn1 = (ImageView) convertView.findViewById(R.id.circleTurn1);
                circleTurn1.setAlpha(130);
                final Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                rotation.setFillAfter(true);
                circleTurn1.startAnimation(rotation);
                final ImageView circleTurn2 = (ImageView) convertView.findViewById(R.id.circleTurn2);
                circleTurn2.setAlpha(130);
                final Animation rotation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate2);
                rotation2.setFillAfter(true);
                circleTurn2.startAnimation(rotation2);

                final ImageView logoPegion = (ImageView) convertView.findViewById(R.id.logoPegion);
                final RelativeLayout layoutTouchToPegion = (RelativeLayout)convertView.findViewById(R.id.layoutTouchToPegion);
                final TextView textUnderLogo = (TextView) convertView.findViewById(R.id.textUnderLogo);

                if(!isStartPegion){
                    logoPegion.setImageResource(R.drawable.logo4);
                    rotation.setDuration(8000);
                    circleTurn1.startAnimation(rotation);
                    rotation2.setDuration(9000);
                    circleTurn2.startAnimation(rotation2);
                    textUnderLogo.setText("Touch to Pegion");
                }else{
                    logoPegion.setImageResource(R.drawable.logopink);
                    rotation.setDuration(2000);
                    circleTurn1.startAnimation(rotation);
                    rotation2.setDuration(3000);
                    circleTurn2.startAnimation(rotation2);
                    textUnderLogo.setText("Running Pegion...");
                }

                Button button = (Button) convertView.findViewById(R.id.startPegion);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isStartPegion){
                            logoPegion.setImageResource(R.drawable.logopink);
                            layoutTouchToPegion.setBackgroundColor(Color.parseColor("#e91e63"));
                            rotation.setDuration(2000);
                            circleTurn1.startAnimation(rotation);
                            rotation2.setDuration(3000);
                            circleTurn2.startAnimation(rotation2);
                            textUnderLogo.setText("Running Pegion...");
                            isStartPegion = true;
                            if (connectionManager == null) {
                                connectionManager = new ConnectionManager(getActivity());
                            }
                            connectionManager.start();
                        }else{
                            logoPegion.setImageResource(R.drawable.logo4);
                            layoutTouchToPegion.setBackgroundColor(Color.parseColor("#303F9F"));
                            rotation.setDuration(8000);
                            circleTurn1.startAnimation(rotation);
                            rotation2.setDuration(9000);
                            circleTurn2.startAnimation(rotation2);
                            textUnderLogo.setText("Touch to Pegion");
                            isStartPegion = false;
                            connectionManager.stop();
                        }
                    }
                });
        }

        return convertView;

    }

    public Activity getActivity() {
        return activity;
    }

    public class ViewHolder {
        TextView title;
        ImageView picture_profile;
        ImageView picture_show;
        TextView description;
        Button sent;
        ImageView show_gps_map;
    }

}

