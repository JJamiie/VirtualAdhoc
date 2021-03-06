package com.example.jjamie.virtualadhoc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static PegionNetworkAdapter pegionNetworkAdapter;
    public static PeopleNearByAdapter peopleNearByAdapter;
    private ListenerNeighbor listenerNeighbor;
    private Boolean is_show_people_nearby = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static ListView listViewPigeonNetwork;
    private static ListView listViewPeopleNearBy;
    private OnFragmentInteractionListener mListener;

    private Button btn_manage_network;
    public static Boolean is_btn_manage_network_click = false;
    private Button btn_create_network;
    private Boolean is_btn_create_network_click = false;
    private TextView txt_manage_network;
    private TextView txt_create_network;
    private RelativeLayout tab_header_pigeon_network;
    private TextView txt_pigeon_network;

    public static Boolean manageIsOn = false;
    public static Boolean createIsOn = false;

    private MateFragment mateFragment;

    public MateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MateFragment newInstance(String param1, String param2) {
        MateFragment fragment = new MateFragment();
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mate, container, false);
        // Add pigeon network adapter
        listViewPigeonNetwork = (ListView) view.findViewById(R.id.list_pigeon_network);
        pegionNetworkAdapter = new PegionNetworkAdapter(getActivity(), view);
        listViewPigeonNetwork.setAdapter(pegionNetworkAdapter);

        btn_manage_network = (Button) view.findViewById(R.id.btn_manage_network);
        btn_create_network = (Button) view.findViewById(R.id.btn_create_network);
        txt_manage_network = (TextView) view.findViewById(R.id.txt_mange_network);
        txt_create_network = (TextView) view.findViewById(R.id.txt_create_network);
        tab_header_pigeon_network = (RelativeLayout) view.findViewById(R.id.tab_header_pigeon_network);
        txt_pigeon_network = (TextView) view.findViewById(R.id.pigeon_network_text);

        btn_manage_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EfficientAdapter.isStartpigeon) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Please stop flying pigeon before enable network.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    if (!is_btn_manage_network_click) {
                        turnManageNetworkOn();
                        TabActivity.freezeConnectionManager();
                    } else {
                        turnManangeNetworkOff();
                        TabActivity.unFreezeConnectionManager();
                    }
                }
            }
        });

        btn_create_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EfficientAdapter.isStartpigeon) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Please stop flying pigeon before create network.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    if (!is_btn_create_network_click) {
                        turnCreateNetworkOn();
                        TabActivity.freezeConnectionManager();
                    } else {
                        turnCreateNetworkOff();
                        TabActivity.unFreezeConnectionManager();
                    }
                }
            }
        });

        // Add people adapter
        listViewPeopleNearBy = (ListView) view.findViewById(R.id.list_people_nearby);
        peopleNearByAdapter = new PeopleNearByAdapter(getActivity(), view);
        listViewPeopleNearBy.setAdapter(peopleNearByAdapter);

        mateFragment = this;
        listenerNeighbor = new ListenerNeighbor(getActivity(), peopleNearByAdapter);
        listenerNeighbor.start();

        if (!is_show_people_nearby) {
            listViewPeopleNearBy.setVisibility(View.VISIBLE);
        } else {
            listViewPeopleNearBy.setVisibility(View.INVISIBLE);
        }

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


    public void turnCreateNetworkOn() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("Are you want to create network ?");
        dialog.setCancelable(true);
        dialog.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        pegionNetworkAdapter.setEnabled_network(false);
                        peopleNearByAdapter.clearNeighbor();
                        showProgressDialog("loading...");
                        ApManager.configApState(getActivity(), true);
                        txt_manage_network.setText("Find network");
                        btn_manage_network.setEnabled(false);
                        is_btn_manage_network_click = false;
                        txt_manage_network.setTextColor(Color.parseColor("#bababa"));
                        txt_create_network.setText("Destroy network");
                        is_btn_create_network_click = true;
                        txt_pigeon_network.setText("People nearby");
                        tab_header_pigeon_network.setVisibility(View.VISIBLE);
                        listViewPeopleNearBy.setVisibility(View.VISIBLE);
                        listViewPigeonNetwork.setVisibility(View.GONE);
                        createIsOn = true;
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


    public void turnCreateNetworkOff() {
        ReportNeighbor.hotspotBroadcastDestroyNetwork();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApManager.configApState(getActivity(), false);
        tab_header_pigeon_network.setVisibility(View.GONE);
        listViewPeopleNearBy.setVisibility(View.GONE);
        btn_manage_network.setEnabled(true);
        txt_manage_network.setTextColor(Color.parseColor("#71717D"));
        txt_create_network.setText("Create network");
        is_btn_create_network_click = false;
        createIsOn = false;

    }


    public void turnManageNetworkOn() {
        tab_header_pigeon_network.setVisibility(View.VISIBLE);
        listViewPeopleNearBy.setVisibility(View.GONE);
        listViewPigeonNetwork.setVisibility(View.VISIBLE);
        txt_manage_network.setText("Stop finding");
        pegionNetworkAdapter.setEnabled_network(true);
        is_btn_manage_network_click = true;
        txt_pigeon_network.setText("Pigeon network");
        manageIsOn = true;

    }

    public void turnManangeNetworkOff() {
        tab_header_pigeon_network.setVisibility(View.INVISIBLE);
        listViewPeopleNearBy.setVisibility(View.GONE);
        listViewPigeonNetwork.setVisibility(View.GONE);
        txt_manage_network.setText("Find network");
        pegionNetworkAdapter.setEnabled_network(false);
        is_btn_manage_network_click = false;

        // if client leave the room , client report to hotspot
        if (PegionNetworkAdapter.leaveNetwork) {
            PegionNetworkAdapter.leaveNetwork = false;
            peopleNearByAdapter.clearNeighbor();
            ReportNeighbor.clientReportLeave();
        }
        manageIsOn = false;
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread() {
            public void run() {
                while (true) {
                    if (ApManager.isApOn(getActivity())) {
                        progressDialog.dismiss();
                        break;
                    }
                }
            }
        }.start();

    }

}
