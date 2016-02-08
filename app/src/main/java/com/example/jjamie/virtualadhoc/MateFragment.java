package com.example.jjamie.virtualadhoc;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


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
    private PegionNetworkAdapter pegionNetworkAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listViewPigeonNetwork;
    private OnFragmentInteractionListener mListener;

    private Button btn_manage_network;
    private Boolean is_btn_manage_network_click = false;
    private Button btn_create_network;
    private Boolean is_btn_create_network_click = false;
    private TextView txt_manage_network;
    private TextView txt_create_network;

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
        btn_manage_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_btn_manage_network_click) {
                    txt_manage_network.setText("Stop finding network");
                    pegionNetworkAdapter.setEnabled_network(true);
                    is_btn_manage_network_click = true;
                } else {
                    txt_manage_network.setText("Find network");
                    pegionNetworkAdapter.setEnabled_network(false);
                    is_btn_manage_network_click = false;
                }
            }
        });

        btn_create_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_btn_create_network_click) {
                    pegionNetworkAdapter.setEnabled_network(false);
                    txt_manage_network.setText("Find network");
                    btn_manage_network.setEnabled(false);
                    is_btn_manage_network_click = false;

                    txt_manage_network.setTextColor(Color.parseColor("#bababa"));
                    txt_create_network.setText("Destroy network");
                    ApManager.configApState(getActivity(), true);
                    is_btn_create_network_click = true;

                } else {
                    btn_manage_network.setEnabled(true);
                    txt_manage_network.setTextColor(Color.parseColor("#71717D"));
                    txt_create_network.setText("Create network");
                    ApManager.configApState(getActivity(), false);
                    is_btn_create_network_click = false;

                }
            }
        });


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
