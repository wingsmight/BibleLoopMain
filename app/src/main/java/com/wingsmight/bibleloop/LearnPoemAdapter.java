package com.wingsmight.bibleloop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;


public class LearnPoemAdapter extends PoemAdapter
{
    private static int activeIndex = -1;


    public LearnPoemAdapter(Context context)
    {
        super(context, Poems.GetPoem(TypePoem.Learn));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        convertView = super.getView(position, convertView, parent);

        LinearLayout knowLayout = convertView.findViewById(R.id.knowLayout);
        if(GetActiveIndex() == position)
        {
            knowLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            knowLayout.setVisibility(View.GONE);
        }

        Button remember = knowLayout.findViewById(R.id.remember);
        Button learnLater = knowLayout.findViewById(R.id.learnLater);
        remember.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MoveToKnow();
            }
        });
        learnLater.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DeletePoem();
            }
        });

        return convertView;
    }

    private void MoveToKnow()
    {
        Poems.AddPoem(TypePoem.Know, GetActivityBlock());
        Poems.RemovePoem(TypePoem.Learn, GetActiveIndex());

        MainActivity.SetKnowCount(Poems.GetPoem(TypePoem.Know).size());
        MainActivity.SetLearnCount(Poems.GetPoem(TypePoem.Learn).size());

        notifyDataSetChanged();
        MainActivity.UpdateAdapters();
    }

    private void DeletePoem()
    {
        FileManager.DeleteFile(new File(GetActivityBlock().GetInternalPath(getContext())));

        Poems.RemovePoem(TypePoem.Learn, GetActiveIndex());
        Poems.RemovePoem(TypePoem.Downloaded, GetActiveIndex());

        MainActivity.SetLearnCount(Poems.GetPoem(TypePoem.Learn).size());

        notifyDataSetChanged();
        MainActivity.UpdateAdapters();
    }

    @Override
    protected int GetTitleColor(String poemTitle)
    {
        return getContext().getResources().getColor(R.color.colorChapterTitleBlockLearn);
    }

    @Override
    public int GetActiveIndex()
    {
        return LearnPoemAdapter.activeIndex;
    }

    @Override
    public void SetActiveIndex(int index)
    {
        LearnPoemAdapter.activeIndex = index;
    }
}
