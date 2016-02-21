package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView listview;
    private EfficientAdapter efficientAdapter;
    private AppBarLayout appBar;
    private MyDatabase myDatabase;
    private SQLiteDatabase sqLiteDatabase;

    public NewFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewFeedFragment newInstance(String param1, String param2) {
        NewFeedFragment fragment = new NewFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    AlbumStorageDirFactory mAlbumStorageDirFactory;
    ListenerPacket listenerPacket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        appBar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        myDatabase = new MyDatabase(getActivity());
        sqLiteDatabase = myDatabase.getWritableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_feed, container, false);
        listview = (ListView) v.findViewById(R.id.listview);
        efficientAdapter = new EfficientAdapter(getActivity(),sqLiteDatabase);

        listview.setAdapter(efficientAdapter);
        //ListenerPacket start
        if (listenerPacket == null) {
            listenerPacket = new ListenerPacket(getActivity(), mAlbumStorageDirFactory, efficientAdapter,sqLiteDatabase,myDatabase);
            listenerPacket.start();
        }
        return v;
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
    public void onResume() {
        super.onResume();
        if (efficientAdapter != null) {
            efficientAdapter.updateTable();
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

    public void onDestroy(){
        super.onDestroy();
        sqLiteDatabase.close();
        myDatabase.close();
    }


}
