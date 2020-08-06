package com.wingsmight.bibleloop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class OrderList extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OrderList.OnFragmentInteractionListener mListener;


    public static OrderList newInstance(String param1, String param2)
    {
        OrderList fragment = new OrderList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        View view = getView();
//
//        mainActivity = new MainActivity();
//        mainContext = MainActivity.GetContext();
//
//        expandableListView = view.findViewById(R.id.expandable_order_list_view);
//        adapter = new ExpandableListViewAdapter(view.getContext());
//        expandableListView.setAdapter(adapter);
//
//        final int[] prevExpandPosition = {-1};
//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
//        {
//            @Override
//            public void onGroupExpand(int groupPosition)
//            {
//                if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != groupPosition)
//                {
//                    expandableListView.collapseGroup(prevExpandPosition[0]);
//                }
//                prevExpandPosition[0] = groupPosition;
//
//                adapter.SetActiveIndex(groupPosition);
//            }
//
//            //findViewById(R.id.remainingTimeLabel).setVisibility(View.GONE);
//        });
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener()
//        {
//            @Override
//            public void onGroupCollapse(int groupPosition)
//            {
//                ((TextView)expandableListView.findViewById(R.id.lyricsTitleView)).setMaxLines(2);
//                adapter.SetActiveIndex(-1);
//            }
//
//            //findViewById(R.id.remainingTimeLabel).setVisibility(View.GONE);
//        });
//
//        //create separator
//        expandableListView.setChildDivider(getResources().getDrawable(R.color.colorLyricsTitleBlock));
//        expandableListView.setDivider(getResources().getDrawable(R.color.colorMainBackground));
//        expandableListView.setDividerHeight(15);
//
//        //indicator to right side
//        MainActivity.SetExpandableListView(expandableListView, 1);
//        MainActivity.SetAdapter(adapter, 1);
//        mainActivity.onWindowFocusChanged(true);
//    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        SetPoemsList();
    }

    private void SetPoemsList()
    {
        View view = getView();

        final ListView listView = view.findViewById(R.id.list);
        final PoemAdapter adapter = new PoemAdapter(getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            final int[] prevExpandPosition = {-1};
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                final ExpandableTextView expandableTextView = adapter.getViewByPosition(position, listView).findViewById(R.id.expandableTextView);

                if (expandableTextView.isExpanded())
                {
                    expandableTextView.collapse();

                    adapter.SetActiveIndex(-1);
                }
                else
                {
                    expandableTextView.expand();

                    if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != position)
                    {
                        ((ExpandableTextView)adapter.getViewByPosition(prevExpandPosition[0], listView).findViewById(R.id.expandableTextView)).collapse();
                    }
                    prevExpandPosition[0] = position;

                    adapter.SetActiveIndex(position);
                }

                adapter.Update();
            }
        });

        //indicator to right side
        MainActivity.SetAdapter(adapter, 1);
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
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        return view;
    }

    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OrderList.OnFragmentInteractionListener)
        {
            mListener = (OrderList.OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
