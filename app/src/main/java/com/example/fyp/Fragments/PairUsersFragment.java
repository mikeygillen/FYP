package com.example.fyp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fyp.Activities.MatchedUserActivity;
import com.example.fyp.Helper.Helper;
import com.example.fyp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PairUsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PairUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PairUsersFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PairUserActivity";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View v;

    private TextView d, p;
    private Button btnSearch;
    private SeekBar sDistance, sPace;
    private RadioGroup rg;
    private RadioButton rb;

    private OnFragmentInteractionListener mListener;

    public PairUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PairUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PairUsersFragment newInstance(String param1, String param2) {
        PairUsersFragment fragment = new PairUsersFragment();
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_pair_users, container, false);

        btnSearch = (Button) v.findViewById(R.id.btn_search_user);
        rg = (RadioGroup) v.findViewById(R.id.RadioGroup);

        sDistance = (SeekBar) v.findViewById(R.id.SeekBarDistance);
        sDistance.setOnSeekBarChangeListener(this);
        sDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                d = (TextView) v.findViewById(R.id.DistanceSet);
                d.setText(Helper.seekbarPercentageConverter(sDistance.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sPace = (SeekBar) v.findViewById(R.id.SeekBarPace);
        sPace.setOnSeekBarChangeListener(this);
        sPace.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = (TextView) v.findViewById(R.id.PaceSet);
                p.setText(Helper.seekbarPercentageConverter(sPace.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchUser();
            }
        });

        return v;
    }

    private void SearchUser() {
        btnSearch.setEnabled(false);

        try {
            int selectedId = rg.getCheckedRadioButtonId();
            rb = (RadioButton) v.findViewById(selectedId);

            int seekDistanceValue = sDistance.getProgress();
            int seekPaceValue = sPace.getProgress();

            if (sDistance==null){
                seekDistanceValue = 100;
            }else if (sPace==null){
                seekPaceValue = 100;
            }
            Toast.makeText(getActivity(), "dis" + seekDistanceValue  + " - Pace: " + seekPaceValue + " - Preference: " + rb.getText(), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getActivity(), MatchedUserActivity.class);
            intent.putExtra("distance_value", String.valueOf(seekDistanceValue));
            intent.putExtra("pace_value", String.valueOf(seekPaceValue));
            intent.putExtra("preferred_value", rb.getText());
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "dis" + 100  + " - Pace: " + 100 + " - Preference: " + "Both", Toast.LENGTH_LONG).show();Intent intent = new Intent(getActivity(), MatchedUserActivity.class);
            intent.putExtra("distance_value", "100");
            intent.putExtra("pace_value", "100");
            intent.putExtra("preferred_value", "Both");
            startActivity(intent);

        }

        btnSearch.setEnabled(true);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sDistance){
            d.setText("Change in Distance = " + Helper.seekbarPercentageConverter(sDistance.getProgress()) + "%");
        }
        else if (seekBar == sPace){
            p.setText("Change in Pace = " + Helper.seekbarPercentageConverter(sPace.getProgress()) + "%");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
