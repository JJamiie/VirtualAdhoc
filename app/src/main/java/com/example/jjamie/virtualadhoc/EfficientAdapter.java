package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by JJamie on 1/2/16 AD.
 */
public class EfficientAdapter extends BaseAdapter {
    public LayoutInflater mInflater;
    public Activity activity;
    private static final int TYPE_MAX_COUNT = 2;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FIRST = 1;
    private SOSManager sosManager;
    public static boolean isStartpigeon = false;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor mCursor;
    public static BaseAdapter adapter;

    public EfficientAdapter(Activity activity,SQLiteDatabase sqLiteDatabase) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        sosManager = new SOSManager(activity);

        this.sqLiteDatabase = sqLiteDatabase;
        // Get all row in picture table in pigeon database
        updateTable();

    }

    @Override
    public int getCount() {
        return mCursor.getCount() + 1;
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
                    holder.gps_zone = (RelativeLayout) convertView.findViewById(R.id.gpsZone);
                    holder.gps_button = (Button) convertView.findViewById(R.id.gps_button);
                    holder.layout_linear = (LinearLayout) convertView.findViewById(R.id.layout_listview);
                    convertView.setTag(holder); //deposit to tag
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                mCursor.moveToPosition(position - 1);
                //Set sender's name
                int columnIndex = mCursor.getColumnIndex(MyDatabase.COL_SENDER_NAME);
                final String senderName = mCursor.getString(columnIndex);
                holder.title.setText(senderName);

                //Set message
                columnIndex = mCursor.getColumnIndex(MyDatabase.COL_MESSAGE);
                final String message = mCursor.getString(columnIndex);
                holder.description.setText(message);

                //Set location
                columnIndex = mCursor.getColumnIndex(MyDatabase.COL_LOCATION);
                final String location = mCursor.getString(columnIndex);
                if (location.equals("null")) {
                    holder.gps_zone.setVisibility(View.INVISIBLE);
                } else {
                    holder.gps_zone.setVisibility(View.VISIBLE);
                }

                //Set picture profile
                Glide.with(activity).load(R.drawable.profile).bitmapTransform(new CropCircleTransformation(activity)).into(holder.picture_profile);

                //Set filename
                columnIndex = mCursor.getColumnIndex(MyDatabase.COL_FILE_NAME);
                final String filename = mCursor.getString(columnIndex);
                final File fileImage = ManageImage.isExist(filename);

                //Set image
                if (fileImage != null) {
                    Glide.with(activity).load(fileImage).centerCrop().placeholder(new ColorDrawable(0xFFc5c4c4)).into(holder.picture_show);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
                    holder.picture_show.setLayoutParams(layoutParams);

                } else {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    holder.picture_show.setLayoutParams(layoutParams);
                }


                //Set sent button
                holder.sent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Sent file");
                        try {
                            if (fileImage != null) {
                                byte[] img = new byte[(int) fileImage.length()];
                                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileImage));
                                buf.read(img, 0, img.length);
                                buf.close();
                                Image image = new Image(senderName, filename, message, location, img);
                                Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);
                            } else {
                                Image image = new Image(senderName, filename, message, location, null);
                                Broadcaster.broadcast(image.getBytes(), ListenerPacket.PORT_PACKET);
                            }

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "Sent", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (IOException ex) {

                        } catch (LengthIncorrectLengthException ex) {

                        }
                    }
                });

                //Set gps button
                holder.gps_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent mapActivityIntent = new Intent(getActivity(), MapsActivity.class);
                                mapActivityIntent.putExtra("location", location);
                                getActivity().startActivity(mapActivityIntent);
                                System.out.println("location" + location);
                            }
                        });
                    }
                });

                columnIndex = mCursor.getColumnIndex("_id");
                final String id = mCursor.getString(columnIndex);
                holder.layout_linear.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showdeleteDialog(id);
                        return false;
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

                final ImageView logoPigeon = (ImageView) convertView.findViewById(R.id.logopigeon);
                final RelativeLayout layoutTouchTopigeon = (RelativeLayout) convertView.findViewById(R.id.layoutTouchToPigeon);
                final TextView textUnderLogo = (TextView) convertView.findViewById(R.id.textUnderLogo);

                if (!isStartpigeon) {
                    logoPigeon.setImageResource(R.drawable.logo4);
                    rotation.setDuration(8000);
                    circleTurn1.startAnimation(rotation);
                    rotation2.setDuration(9000);
                    circleTurn2.startAnimation(rotation2);
                    textUnderLogo.setText("Touch to pigeon");
                } else {
                    logoPigeon.setImageResource(R.drawable.logopink);
                    rotation.setDuration(2000);
                    circleTurn1.startAnimation(rotation);
                    rotation2.setDuration(3000);
                    circleTurn2.startAnimation(rotation2);
                    textUnderLogo.setText("Flying Pigeon...");
                    layoutTouchTopigeon.setBackgroundColor(Color.parseColor("#e91e63"));
                }

                final Button button = (Button) convertView.findViewById(R.id.startpigeon);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (MateFragment.manageIsOn) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Please stop finding network before flying pigeon.", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (MateFragment.createIsOn) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Please destroy network before flying pigeon.", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {

                            if (!isStartpigeon) {
                                logoPigeon.setImageResource(R.drawable.logopink);
                                layoutTouchTopigeon.setBackgroundColor(Color.parseColor("#e91e63"));
                                rotation.setDuration(2000);
                                circleTurn1.startAnimation(rotation);
                                rotation2.setDuration(3000);
                                circleTurn2.startAnimation(rotation2);
                                textUnderLogo.setText("Flying Pigeon...");
                                isStartpigeon = true;

                                if (sosManager.getState() == Thread.State.NEW) {
                                    sosManager.start();
                                }
                                synchronized (sosManager) {
                                    sosManager.wake();
                                }


                                button.setEnabled(false);

                                Timer buttonTimer = new Timer();
                                buttonTimer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                button.setEnabled(true);
                                            }
                                        });
                                    }
                                }, 800);


                            } else {
                                logoPigeon.setImageResource(R.drawable.logo4);
                                layoutTouchTopigeon.setBackgroundColor(Color.parseColor("#303F9F"));
                                rotation.setDuration(8000);
                                circleTurn1.startAnimation(rotation);
                                rotation2.setDuration(9000);
                                circleTurn2.startAnimation(rotation2);
                                textUnderLogo.setText("Touch to pigeon");
                                isStartpigeon = false;
                                sosManager.sleep();
                                sosManager.interrupt();
                                System.out.println("sos manager sleep");
                            }
                        }
                    }
                });
        }

        return convertView;

    }

    public void updateTable() {
        mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_PICTURE + " ORDER BY _id DESC", null);
        mCursor.moveToFirst();
        notifyDataSetChanged();
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
        RelativeLayout gps_zone;
        Button gps_button;
        LinearLayout layout_linear;
    }


    public void showdeleteDialog(final String id) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Delete this message.");
        arrayAdapter.add("Delete all messages.");


        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        if (strName.equals("Delete this message.")) {
                            sqLiteDatabase.delete(MyDatabase.TABLE_NAME_PICTURE, "_id = " + id, null);
                        } else {
                            sqLiteDatabase.delete(MyDatabase.TABLE_NAME_PICTURE, null, null);
                        }
                        updateTable();
                    }
                });
        builderSingle.show();
    }


}

