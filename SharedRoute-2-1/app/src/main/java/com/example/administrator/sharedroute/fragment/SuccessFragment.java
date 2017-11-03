package com.example.administrator.sharedroute.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.ConfirmFinishedAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SuccessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuccessFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<listItem> listItemList ;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private ConfirmFinishedAdapter adapter;
    private AnimationAdapter mAnimAdapter;
    public SuccessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuccessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SuccessFragment newInstance(String param1, String param2) {
        SuccessFragment fragment = new SuccessFragment();
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
        View view =inflater.inflate(R.layout.fragment_success, container, false);
        listView = (ListView)(view.findViewById(R.id.success_fragment_listView));
        adapter = new ConfirmFinishedAdapter(getActivity());
        Bundle bundle = getArguments();
        listItemList= bundle.getParcelableArrayList("listItemList");
        for (listItem e:listItemList) {
            adapter.add(e);
        }
        mAnimAdapter = new SwingBottomInAnimationAdapter(adapter);
        mAnimAdapter.setAbsListView(listView);
        listView.setAdapter(mAnimAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


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
}
