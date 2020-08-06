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


public class KnowList extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private MediaPlayer mediaPlayer;

    private static Context mainContext;
    private DownloadManager downloadManager;
    private MainActivity mainActivity;
    public static String mainPath;

    private OnFragmentInteractionListener mListener;


    public static KnowList newInstance(String param1, String param2)
    {
        KnowList fragment = new KnowList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity = new MainActivity();
        mainContext = MainActivity.GetContext();

        SetPoemsList();
    }

    private void SetPoemsList()
    {
        View view = getView();

        final ListView listView = view.findViewById(R.id.list);
        final KnowPoemAdapter adapter = new KnowPoemAdapter(getContext());
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

        MainActivity.SetAdapter(adapter, 2);
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
        return inflater.inflate(R.layout.fragment_know, container, false);
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
