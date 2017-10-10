package com.example.administrator.sharedroute.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.SearchNeedsRcViewAdapter;

import java.util.ArrayList;

/**
 * Created by luodian on 04/10/2017.
 */

public class TwoFragment extends Fragment {
    private ArrayList<String> myDataset;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.campus1_search_needs, container, false);
        RecyclerView mrc = (RecyclerView) view.findViewById(R.id.searchNeeds_recycler_view);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
        mrc.setHasFixedSize(true);
//
//        // use a linear layout manager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mrc.setLayoutManager(llm);
//
//        // specify an adapter (see also next example)

        init_data();
        SearchNeedsRcViewAdapter adapter = new SearchNeedsRcViewAdapter(myDataset);
        mrc.setAdapter(adapter);
        return view;
    }

    protected void init_data()
    {
        myDataset = new ArrayList<>();
        myDataset.add("Apple");
        myDataset.add("Amazon");
        myDataset.add("Microsoft");
        myDataset.add("Google");
        myDataset.add("Samsung");
        myDataset.add("Intel");
    }
}
