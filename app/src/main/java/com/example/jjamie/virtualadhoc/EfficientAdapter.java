package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

    public EfficientAdapter(Activity activity) {
        this.mContext = activity.getApplicationContext();
        this.activity = activity;
        mInflater = LayoutInflater.from(mContext);
        adapter = this;
    }

    @Override
    public int getCount() {
        if(ManageImage.getFile()==null) return 0;
        return ManageImage.getFile().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    int sequenceNumber = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            //load layout
            convertView = mInflater.inflate(R.layout.item_listview, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_listview_name);
            holder.picture_profile = (ImageView) convertView.findViewById(R.id.item_listview_profile_picture);
            holder.picture_show = (ImageView) convertView.findViewById(R.id.item_picture);
            holder.description = (TextView) convertView.findViewById(R.id.item_listview_description);
            holder.sent = (Button) convertView.findViewById(R.id.sent);
            holder.show_gps_map = (ImageView) convertView.findViewById(R.id.show_gps_map);
            convertView.setTag(holder); //deposit to tag
        } else {
            //rebind widget
            holder = (ViewHolder) convertView.getTag();

        }

        //Set value each item in list
        //load contact and description
        String contact="";
        String caption="";
        try {
            com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(ManageImage.getFile()[position]);
            for (Directory directory : metadata.getDirectories()) {
                for (com.drew.metadata.Tag tag : directory.getTags()) {
                    if(tag.getTagName().equals("Contact")){
                        contact = tag.getDescription();
                    }else if(tag.getTagName().equals("Keywords")){
                        caption = tag.getDescription();
                    }else if(tag.getTagName().equals("Sub-location")){
                        if(!tag.getDescription().equals("null")){
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
        final File fileImage = new File(ManageImage.getFile()[position].getPath());
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
        holder.show_gps_map.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
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

