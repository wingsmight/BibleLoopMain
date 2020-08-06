package com.wingsmight.bibleloop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Poems
{
    private static ArrayList<LyricsBlock> all;
    private static ArrayList<LyricsBlock> downloaded;
    private static ArrayList<LyricsBlock> learn;
    private static ArrayList<LyricsBlock> know;
    private static ArrayList<LyricsBlock> startDownloaded;
    private static ArrayList<LyricsBlock>[] chapter;


    public static void CreatePoems()
    {
        if(Poems.all != null) return;

        Poems.all = new ArrayList<LyricsBlock>();
        Poems.downloaded = new ArrayList<LyricsBlock>();
        Poems.chapter = new ArrayList[60];
        Poems.learn = new ArrayList<LyricsBlock>();
        Poems.know = new ArrayList<LyricsBlock>();
        Poems.startDownloaded = new ArrayList<LyricsBlock>();

        JsonManager jsonManage = new JsonManager();

        int curIndex = 0;
        int curChapterIndex = 0;
        int indexInChapter = 0;
        ArrayList<LyricsBlock> sameChapterPoems = new ArrayList<LyricsBlock>();
        String prevChapter;
        try
        {
            JSONArray jsonLyricsBlocks = jsonManage.ReadJsonArray("Lyrics");
            prevChapter = jsonLyricsBlocks.getJSONObject(0).getString("Chapter");

            for (int i = 0; i < Poems.chapter.length; i++) {
                Poems.chapter[i] = new ArrayList<LyricsBlock>();
            }

            while(true)
            {
                JSONObject lyrics = jsonLyricsBlocks.getJSONObject(curIndex++);

                String chapter = lyrics.getString("Chapter");
                String title = lyrics.getString("Title");
                String lyricsFull = lyrics.getString("Lyrics");
                int bibleIndex = lyrics.getInt("BibleIndex");

                if(!prevChapter.equals(chapter))
                {
                    for (LyricsBlock poem : sameChapterPoems)
                    {
                        poem.SetChpterSize(indexInChapter);
                    }
                    sameChapterPoems.clear();
                    indexInChapter = 0;
                    curChapterIndex++;
                }

                LyricsBlock newLyricsBlock = new LyricsBlock(chapter, title, lyricsFull, bibleIndex, curChapterIndex, indexInChapter);
                if(chapter.equals("Псалмы") || chapter.equals("Дополнительно"))
                {
                    newLyricsBlock.SetTtitleFull(lyrics.getString("Title"));
                }
                Poems.all.add(newLyricsBlock);
                sameChapterPoems.add(newLyricsBlock);

                if(SaveLoadData.LoadExistPoem(TypePoem.Downloaded, title))
                {
                    Poems.downloaded.add(newLyricsBlock);
                }

                if(SaveLoadData.LoadExistPoem(TypePoem.Know, title))
                {
                    Poems.know.add(newLyricsBlock);
                }
                else if(SaveLoadData.LoadExistPoem(TypePoem.Learn, title))
                {
                    Poems.learn.add(newLyricsBlock);
                }

                indexInChapter++;
                Poems.chapter[curChapterIndex].add(newLyricsBlock);

                prevChapter = chapter;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void AddPoem(TypePoem typePoem, LyricsBlock poem)
    {
        switch (typePoem)
        {
            case All:
                Poems.all.add(poem);
                break;

            case Downloaded:
                Poems.downloaded.add(poem);
                break;

            case Learn:
                Poems.learn.add(poem);
                break;

            case Know:
                Poems.know.add(poem);
                break;

            case StartDownLoaded:
                Poems.startDownloaded.add(poem);
                break;
        }

        SaveLoadData.SetSaveExistPoem(typePoem, poem.GetTitle(), true);
    }

    public static void RemovePoem(TypePoem typePoem, int index)
    {
        switch (typePoem)
        {
            case All:
                SaveLoadData.SetSaveExistPoem(typePoem,  Poems.all.get(index).GetTitle(), false);
                Poems.all.remove(index);
                break;

            case Downloaded:
                SaveLoadData.SetSaveExistPoem(typePoem,  Poems.downloaded.get(index).GetTitle(), false);
                Poems.downloaded.remove(index);
                break;

            case Learn:
                SaveLoadData.SetSaveExistPoem(typePoem,  Poems.learn.get(index).GetTitle(), false);
                Poems.learn.remove(index);
                break;

            case Know:
                SaveLoadData.SetSaveExistPoem(typePoem,  Poems.know.get(index).GetTitle(), false);
                Poems.know.remove(index);
                break;

            case StartDownLoaded:
                SaveLoadData.SetSaveExistPoem(typePoem,  Poems.startDownloaded.get(index).GetTitle(), false);
                Poems.startDownloaded.remove(index);
                break;
        }


    }
    public static void RemovePoem(TypePoem typePoem, LyricsBlock poem)
    {
        switch (typePoem)
        {
            case All:
                Poems.all.remove(poem);
                break;

            case Downloaded:
                Poems.downloaded.remove(poem);
                break;

            case Learn:
                Poems.learn.remove(poem);
                break;

            case Know:
                Poems.know.remove(poem);
                break;

            case StartDownLoaded:
                Poems.startDownloaded.remove(poem);
                break;
        }

        SaveLoadData.SetSaveExistPoem(typePoem, poem.GetTitle(), false);
    }

    public static ArrayList<LyricsBlock> GetPoem(TypePoem typePoem)
    {
        switch (typePoem)
        {
            case All:
                return Poems.all;

            case Downloaded:
                return Poems.downloaded;

            case Learn:
                return Poems.learn;

            case Know:
                return Poems.know;

            case StartDownLoaded:
                return Poems.startDownloaded;
        }

        return null;
    }

    public static LyricsBlock GetPoemByTitle(String title)
    {
        for(int i = 0; i < Poems.all.size(); i++)
        {
            if(Poems.all.get(i).GetTitle().equals(title))
            {
                return Poems.all.get(i);
            }
        }

        return null;
    }

    public static ArrayList<LyricsBlock> GetChapterPoem(int chapterIndex)
    {
        return Poems.chapter[chapterIndex];
    }

    public static boolean IsExist(TypePoem typePoem, String title)
    {
        switch (typePoem)
        {
            case All:
                for (LyricsBlock poem : Poems.all)
                {
                    if(poem.GetTitle().equals(title))
                    {
                        return  true;
                    }
                }

            case Downloaded:
                for (LyricsBlock poem : Poems.downloaded)
                {
                    if(poem.GetTitle().equals(title))
                    {
                        return  true;
                    }
                }

            case Learn:
                for (LyricsBlock poem : Poems.learn)
                {
                    if(poem.GetTitle().equals(title))
                    {
                        return  true;
                    }
                }

            case Know:
                for (LyricsBlock poem : Poems.know)
                {
                    if(poem.GetTitle().equals(title))
                    {
                        return  true;
                    }
                }

            case StartDownLoaded:
                for (LyricsBlock poem : Poems.startDownloaded)
                {
                    if(poem.GetTitle().equals(title))
                    {
                        return  true;
                    }
                }
        }

        return false;
    }
}