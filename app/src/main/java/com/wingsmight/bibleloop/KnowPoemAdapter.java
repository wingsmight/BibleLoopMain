package com.wingsmight.bibleloop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class KnowPoemAdapter extends PoemAdapter
{
    private static int activeIndex = -1;

    public KnowPoemAdapter(Context context)
    {
        super(context, Poems.GetPoem(TypePoem.Know));
    }


    @Override
    protected int GetTitleColor(String poemTitle)
    {
        return getContext().getResources().getColor(R.color.colorChapterTitleBlockKnow);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        convertView = super.getView(position, convertView, parent);

        Button learnAgain = convertView.findViewById(R.id.learnAgain);
        if(GetActiveIndex() == position)
        {
            learnAgain.setVisibility(View.VISIBLE);
        }
        else
        {
            learnAgain.setVisibility(View.GONE);
        }


        learnAgain.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MoveToLearn();
            }
        });

        return convertView;
    }

    private void MoveToLearn()
    {
        Poems.AddPoem(TypePoem.Learn, GetActivityBlock());
        Poems.RemovePoem(TypePoem.Know, GetActiveIndex());

        MainActivity.SetKnowCount(Poems.GetPoem(TypePoem.Know).size());
        MainActivity.SetLearnCount(Poems.GetPoem(TypePoem.Learn).size());

        notifyDataSetChanged();
        MainActivity.UpdateAdapters();
    }

    @Override
    public int GetActiveIndex()
    {
        return KnowPoemAdapter.activeIndex;
    }

    @Override
    public void SetActiveIndex(int index)
    {
        KnowPoemAdapter.activeIndex = index;
    }
}
