package com.wingsmight.bibleloop;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class PageAdapter extends FragmentStatePagerAdapter {

    private int tabsCount;

    public PageAdapter(FragmentManager fm, int NumberOfTabs)
    {
        super(fm);
        tabsCount = NumberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                ChapterList chapterList = new ChapterList();
                return chapterList;
            case 1:
                LearnList learnList = new LearnList();
                return learnList;
            case 2:
                KnowList knowList = new KnowList();
                return knowList;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabsCount;
    }
}