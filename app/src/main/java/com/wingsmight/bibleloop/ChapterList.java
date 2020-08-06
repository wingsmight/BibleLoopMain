package com.wingsmight.bibleloop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;


public class ChapterList extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ExpandableListView expandableListView;
    private ChapterListAdapter adapter;
    private MainActivity mainActivity;


    public static ChapterList newInstance(String param1, String param2)
    {
        ChapterList fragment = new ChapterList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        mainActivity = new MainActivity();

        expandableListView = view.findViewById(R.id.expandable_chapter_list_view);
        adapter = new ChapterListAdapter(view.getContext());
        expandableListView.setAdapter(adapter);

        final int[] prevExpandPosition = {-1};
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
            @Override
            public void onGroupExpand(int groupPosition)
            {
                if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != groupPosition)
                {
                    expandableListView.collapseGroup(prevExpandPosition[0]);
                }
                prevExpandPosition[0] = groupPosition;

                adapter.SetActiveIndex(groupPosition);

                if(MainActivity.GetAdapter(0) != null)
                {
                    MainActivity.GetAdapter(0).SetActiveIndex(-1);
                }
                MainActivity.UpdateAdapter(0);
            }
        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener()
        {
            @Override
            public void onGroupCollapse(int groupPosition)
            {
                adapter.SetActiveIndex(-1);
            }
        });

        //create separator
        expandableListView.setChildDivider(getResources().getDrawable(R.color.colorLyricsTitleBlock));
        expandableListView.setDivider(getResources().getDrawable(R.color.colorMainBackground));
        expandableListView.setDividerHeight(15);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_chapter_list, container, false);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}
