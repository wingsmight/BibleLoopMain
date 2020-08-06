package com.wingsmight.bibleloop;

import android.content.Context;


public class ChapterPoemAdapter extends PoemAdapter
{
    public static int activeIndex = -1;

    public ChapterPoemAdapter(Context context, int chapterIndex)
    {
        super(context, Poems.GetChapterPoem(chapterIndex));
    }

    @Override
    public int GetActiveIndex()
    {
        return ChapterPoemAdapter.activeIndex;
    }

    @Override
    public void SetActiveIndex(int index)
    {
        ChapterPoemAdapter.activeIndex = index;
    }
}
