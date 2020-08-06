package com.wingsmight.bibleloop;

import android.content.Context;

public class LyricsBlock
{
    private String chapter;
    private String title;
    private String titleFull;
    private String lyricsFull;
    private int bibleIndex;
    private int chapterIndex;
    private int indexInChapter;
    private int chapterSize;


    public LyricsBlock(String chapter, String title, String lyricsFull, int bibleIndex, int chapterIndex, int indexInChapter)
    {
        this.chapter = chapter;
        this.title = title;
        this.lyricsFull = lyricsFull;
        this.bibleIndex = bibleIndex;
        this.chapterIndex = chapterIndex;
        this.indexInChapter = indexInChapter;
    }

    public String GetChapter()
    {
        return chapter;
    }
    public int GetChapterIndex()
    {
        return chapterIndex;
    }
    public String GetTitle()
    {
        return title;
    }
    public String GetLyricsFull()
    {
        return lyricsFull;
    }
    public int GetBibleIndex()
    {
        return bibleIndex;
    }
    public String GetTtitleFull()
    {
        return titleFull;
    }
    public void SetTtitleFull(String titleFull)
    {
        this.titleFull = titleFull;
    }
    public String GetInternalPath(Context context)
    {
        String poemTitle = FileManager.PoemTitleToFileName(title);
        String filesExtension= ".mp3";

        return context.getFilesDir().toString() + "/" + poemTitle + filesExtension;
    }
    public int GetIndexInChapter()
    {
        return indexInChapter;
    }
    public int GetChapterSize()
    {
        return chapterSize;
    }
    public void SetChpterSize(int chapterSize)
    {
        this.chapterSize = chapterSize;
    }
}
