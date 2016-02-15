package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MyDatabase myDatabase;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor mCursor;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton fab_edit;
    private TextView txt_email;
    private TextView txt_username;
    private TextView txt_name;
    private TextView txt_surename;
    private TextView txt_sex;
    private TextView txt_birthday;
    private TextView txt_address;

    private EditText edit_txt_email;
    private EditText edit_txt_username;
    private EditText edit_txt_name;
    private EditText edit_txt_surename;
    private EditText edit_txt_sex;
    private EditText edit_txt_birthday;
    private EditText edit_txt_address;

    private Boolean edit = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // create database
        myDatabase = new MyDatabase(getActivity());
        sqLiteDatabase = myDatabase.getWritableDatabase();

        mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_USER + " WHERE username = '" + TabActivity.senderName + "'", null);
        if (mCursor.getCount() == 0) {
            System.out.println("Create new username row");
            myDatabase.addToTableUser(sqLiteDatabase, TabActivity.senderName, "", "", "", "", "", "", "");
            mCursor = sqLiteDatabase.rawQuery("SELECT * FROM " + MyDatabase.TABLE_NAME_USER + " WHERE username = '" + TabActivity.senderName + "'", null);
        }
        mCursor.moveToFirst();

        fab_edit = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit);
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edit) {
                    edit = true;
                    fab_edit.setImageResource(R.drawable.correct);

                    edit_txt_email.setVisibility(View.VISIBLE);
                    txt_email.setVisibility(View.GONE);

                    edit_txt_username.setVisibility(View.VISIBLE);
                    txt_username.setVisibility(View.GONE);

                    edit_txt_name.setVisibility(View.VISIBLE);
                    txt_name.setVisibility(View.GONE);

                    edit_txt_surename.setVisibility(View.VISIBLE);
                    txt_surename.setVisibility(View.GONE);

                    edit_txt_sex.setVisibility(View.VISIBLE);
                    txt_sex.setVisibility(View.GONE);

                    edit_txt_birthday.setVisibility(View.VISIBLE);
                    txt_birthday.setVisibility(View.GONE);

                    edit_txt_address.setVisibility(View.VISIBLE);
                    txt_address.setVisibility(View.GONE);
                } else {
                    edit = false;
                    fab_edit.setImageResource(R.drawable.edit);
                    edit_txt_email.setVisibility(View.GONE);
                    txt_email.setVisibility(View.VISIBLE);
                    String email = edit_txt_email.getText().toString();
                    txt_email.setText(email);

                    edit_txt_username.setVisibility(View.GONE);
                    txt_username.setVisibility(View.VISIBLE);
                    String username = edit_txt_username.getText().toString();
                    txt_username.setText(username);
                    TabActivity.senderName = username;

                    edit_txt_name.setVisibility(View.GONE);
                    txt_name.setVisibility(View.VISIBLE);
                    String name = edit_txt_name.getText().toString();
                    txt_name.setText(name);

                    edit_txt_surename.setVisibility(View.GONE);
                    txt_surename.setVisibility(View.VISIBLE);
                    String surename = edit_txt_surename.getText().toString();
                    txt_surename.setText(surename);

                    edit_txt_sex.setVisibility(View.GONE);
                    txt_sex.setVisibility(View.VISIBLE);
                    String sex = edit_txt_sex.getText().toString();
                    txt_sex.setText(sex);

                    edit_txt_birthday.setVisibility(View.GONE);
                    txt_birthday.setVisibility(View.VISIBLE);
                    String birthday = edit_txt_birthday.getText().toString();
                    txt_birthday.setText(birthday);

                    edit_txt_address.setVisibility(View.GONE);
                    txt_address.setVisibility(View.VISIBLE);
                    String address = edit_txt_address.getText().toString();
                    txt_address.setText(address);

                    myDatabase.updateToTableUser(sqLiteDatabase,username,email,name,surename,sex,birthday,"",address);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        //bind object

        //Image
        ImageView profile_picture = (ImageView) view.findViewById(R.id.profile_picture);
        Glide.with(this).load(R.drawable.my_profile_picture).placeholder(new ColorDrawable(0xFFc5c4c4)).bitmapTransform(new CropCircleTransformation(getContext())).into(profile_picture);
        ImageView profile_picture_background = (ImageView) view.findViewById(R.id.profile_picture_background);
        Glide.with(this).load(R.drawable.my_profile_picture).placeholder(new ColorDrawable(0xFFc5c4c4)).bitmapTransform(new BlurTransformation(getContext())).into(profile_picture_background);

        txt_email = (TextView) view.findViewById(R.id.txt_email);
        int columnIndex = mCursor.getColumnIndex(MyDatabase.COL_EMAIL);
        txt_email.setText(mCursor.getString(columnIndex));

        edit_txt_email = (EditText) view.findViewById(R.id.edit_txt_email);
        edit_txt_email.setText(mCursor.getString(columnIndex));

        txt_username = (TextView) view.findViewById(R.id.txt_username);
        columnIndex = mCursor.getColumnIndex(MyDatabase.COL_USERNAME);
        txt_username.setText(mCursor.getString(columnIndex));

        edit_txt_username = (EditText) view.findViewById(R.id.edit_txt_username);
        edit_txt_username.setText(mCursor.getString(columnIndex));

        txt_name = (TextView) view.findViewById(R.id.txt_name);
        columnIndex = mCursor.getColumnIndex(MyDatabase.COL_NAME);
        txt_name.setText(mCursor.getString(columnIndex));

        edit_txt_name = (EditText) view.findViewById(R.id.edit_txt_name);
        edit_txt_name.setText(mCursor.getString(columnIndex));

        txt_surename = (TextView) view.findViewById(R.id.txt_surename);
        columnIndex = mCursor.getColumnIndex(MyDatabase.COL_SURENAME);
        txt_surename.setText(mCursor.getString(columnIndex));

        edit_txt_surename = (EditText) view.findViewById(R.id.edit_txt_surename);
        edit_txt_surename.setText(mCursor.getString(columnIndex));

        txt_sex = (TextView) view.findViewById(R.id.txt_sex);
        columnIndex = mCursor.getColumnIndex(MyDatabase.COL_SEX);
        txt_sex.setText(mCursor.getString(columnIndex));

        edit_txt_sex = (EditText) view.findViewById(R.id.edit_txt_sex);
        edit_txt_sex.setText(mCursor.getString(columnIndex));

        txt_birthday = (TextView) view.findViewById(R.id.txt_birthdate);
        columnIndex = mCursor.getColumnIndex(MyDatabase.COL_BIRTHDATE);
        txt_birthday.setText(mCursor.getString(columnIndex));

        edit_txt_birthday = (EditText) view.findViewById(R.id.edit_txt_birthdate);
        edit_txt_birthday.setText(mCursor.getString(columnIndex));

        txt_address = (TextView) view.findViewById(R.id.txt_address);
        columnIndex = mCursor.getColumnIndex(MyDatabase.COL_ADDRESS);
        txt_address.setText(mCursor.getString(columnIndex));

        edit_txt_address = (EditText) view.findViewById(R.id.edit_txt_address);
        edit_txt_address.setText(mCursor.getString(columnIndex));


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
