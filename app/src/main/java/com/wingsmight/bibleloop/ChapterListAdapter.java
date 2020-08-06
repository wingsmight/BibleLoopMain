package com.wingsmight.bibleloop;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ChapterListAdapter extends BaseExpandableListAdapter
{
    private List<String> chaptersBlock;
    private Context context;
    private static int activeIndex = -1;


    public ChapterListAdapter(Context context)
    {
        this.context = context;

        chaptersBlock = ReadChapters();
    }

    private List<String> ReadChapters()
    {
        chaptersBlock = new ArrayList<>();
        String chapter = "";

        int curIndex = 0;
        while(curIndex != Poems.GetPoem(TypePoem.All).size())
        {
            String chapter1 = Poems.GetPoem(TypePoem.All).get(curIndex).GetChapter();

            if(!chapter1.equals(chapter))
            {
                chapter = chapter1;

                chaptersBlock.add(chapter);
            }

            curIndex++;
        }

        return chaptersBlock;
    }

    @Override
    public int getGroupCount()
    {
        return chaptersBlock.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return chaptersBlock.get(groupPosition);
    }

    @Override
    public LyricsBlock getChild(int groupPosition, int childPosition)
    {
        return Poems.GetChapterPoem(GetActiveIndex()).get(groupPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String chapterTitle = ((String) getGroup(groupPosition)).toUpperCase();

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chapter_title, null);
        }

        TextView chapterTitleText = convertView.findViewById(R.id.chapterTitleText);
        chapterTitleText.setText(chapterTitle);

        ImageView arrow = convertView.findViewById(R.id.arrow);
        if(GetActiveIndex() == groupPosition)
        {
            arrow.animate().rotation(180).setDuration(100).start();
        }
        else
        {
            arrow.animate().rotation(0).setDuration(100).start();
        }

        return  convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chapter_detailed, null);
        }

        SetPoemsList(convertView, groupPosition);

        return  convertView;
    }

    private void SetPoemsList(View view, int chapterIndex)
    {
        final ListView listView = view.findViewById(R.id.list);
        final ChapterPoemAdapter adapter = new ChapterPoemAdapter(this.context, chapterIndex);
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            ListView lv = view.findViewById(R.id.list);  // your listview inside scrollview
            lv.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
        }

        if(chapterIndex == GetActiveIndex())
        {
            MainActivity.SetAdapter(adapter, 0);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    public String GetActivityChapter()
    {
        return chaptersBlock.get(activeIndex);
    }

    public  int GetActiveIndex()
    {
        return activeIndex;
    }

    public void SetActiveIndex(int activeIndex)
    {
        this.activeIndex = activeIndex;
    }

    public LyricsBlock GetActivityBlock()
    {
        return this.getChild(activeIndex, 0);
    }

    public String GetActivityTitle()
    {
        return this.getChild(activeIndex, 0).GetTitle();
    }

    public void Update()
    {
        notifyDataSetChanged();
    }
}
