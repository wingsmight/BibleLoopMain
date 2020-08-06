package com.wingsmight.bibleloop;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.com.simplepass.loadingbutton.animatedDrawables.ProgressType;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import br.com.simplepass.loadingbutton.presentation.State;

public class PoemAdapter extends ArrayAdapter<LyricsBlock>
{
    private static int activeIndex = -1;

    public PoemAdapter(Context context)
    {
        super(context, R.layout.poem_view, Poems.GetPoem(TypePoem.All));
    }

    public PoemAdapter(Context context, List<LyricsBlock> lyricsBlock)
    {
        super(context, R.layout.poem_view, lyricsBlock);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LyricsBlock lyricsBlock = getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(MainActivity.GetContext()).inflate(R.layout.poem_view, null);
        }

        ExpandableTextView expandableTextView = convertView.findViewById(R.id.expandableTextView);
        SpannableString ss1=  new SpannableString(lyricsBlock.GetTitle());
        ss1.setSpan(new RelativeSizeSpan(1.1f), 0,lyricsBlock.GetTitle().length(), 0); // set size
        ss1.setSpan(new ForegroundColorSpan(GetTitleColor(lyricsBlock.GetTitle())), 0, lyricsBlock.GetTitle().length(), 0);// set color

        SpannableString ss12 =  new SpannableString(lyricsBlock.GetLyricsFull());

        CharSequence finalText = TextUtils.concat(ss1, "  ", ss12);

        expandableTextView.setText(finalText);

        ImageView arrow = convertView.findViewById(R.id.arrow);
        LinearLayout downloadLayout = convertView.findViewById(R.id.downloadLayout);
        LinearLayout listenLayout = convertView.findViewById(R.id.listenLayout);

        TextView textForLittle = convertView.findViewById(R.id.textForLittle);

        if(GetActiveIndex() == position)
        {
            arrow.animate().rotation(180).setDuration(100).start();

            if(SaveLoadData.LoadExistPoem(TypePoem.Downloaded, lyricsBlock.GetTitle()))
            {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
//                {
//                    textForLittle.setVisibility(View.VISIBLE);
//                    textForBig.setVisibility(View.GONE);
//                }
//                else
//                {
//                    textForLittle.setVisibility(View.GONE);
//                    textForBig.setVisibility(View.VISIBLE);
//                }
                textForLittle.setVisibility(View.VISIBLE);

                listenLayout.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.GONE);
            }
            else
            {
                //if(isStartDownload)
                if(Poems.IsExist(TypePoem.StartDownLoaded, lyricsBlock.GetTitle()))
                {
                    CircularProgressButton btn = convertView.findViewById(R.id.btn_id);
                    if(btn.getState() != State.PROGRESS)
                    {
                        btn.startMorphAnimation();
                    }


                    //show progress bar
                }
                else
                {
                    CircularProgressButton btn = convertView.findViewById(R.id.btn_id);
                    btn.setHighlightColor(getContext().getResources().getColor(R.color.colorLyricsTitle));

                    textForLittle.setVisibility(View.GONE);

                    listenLayout.setVisibility(View.GONE);
                    downloadLayout.setVisibility(View.VISIBLE);
                }
            }

            ImageButton button5 = convertView.findViewById(R.id.loop5);
            ImageButton button20 = convertView.findViewById(R.id.loop20);
            ImageButton button50 = convertView.findViewById(R.id.loop50);

            TextView loop5TextPayment = convertView.findViewById(R.id.loop5TextPayment);
            TextView loop5TextNotPayment = convertView.findViewById(R.id.loop5TextNotPayment);
            TextView loop20TextPayment = convertView.findViewById(R.id.loop20TextPayment);
            TextView loop20TextNotPayment = convertView.findViewById(R.id.loop20TextNotPayment);
            TextView loop50TextPayment = convertView.findViewById(R.id.loop50TextPayment);
            TextView loop50TextNotPayment = convertView.findViewById(R.id.loop50TextNotPayment);

            if(MainActivity.isPayment)
            {
                button5.setImageResource(android.R.color.transparent);
                button20.setImageResource(android.R.color.transparent);
                button50.setImageResource(android.R.color.transparent);

                loop5TextPayment.setVisibility(View.VISIBLE);
                loop20TextPayment.setVisibility(View.VISIBLE);
                loop50TextPayment.setVisibility(View.VISIBLE);

                loop5TextNotPayment.setVisibility(View.GONE);
                loop20TextNotPayment.setVisibility(View.GONE);
                loop50TextNotPayment.setVisibility(View.GONE);
            }
            else
            {
                button20.setImageResource(R.drawable.lock);
                button50.setImageResource(R.drawable.lock);

                loop20TextNotPayment.setVisibility(View.VISIBLE);
                loop50TextNotPayment.setVisibility(View.VISIBLE);

                loop20TextPayment.setVisibility(View.GONE);
                loop50TextPayment.setVisibility(View.GONE);

                if(lyricsBlock.GetIndexInChapter() > lyricsBlock.GetChapterSize() / 2 && !lyricsBlock.GetChapter().equals("БПУФ (Библейские принципы управления финансами)"))
                {
                    button5.setImageResource(R.drawable.lock);
                    loop5TextNotPayment.setVisibility(View.VISIBLE);
                    loop5TextPayment.setVisibility(View.GONE);
                }
                else
                {
                    button5.setImageResource(android.R.color.transparent);
                    loop5TextNotPayment.setVisibility(View.GONE);
                    loop5TextPayment.setVisibility(View.VISIBLE);
                }
            }

        }
        else
        {
            arrow.animate().rotation(0).setDuration(100).start();

            textForLittle.setVisibility(View.GONE);
            listenLayout.setVisibility(View.GONE);
            downloadLayout.setVisibility(View.GONE);
        }

        if(expandableTextView.getMaxLines() > 2 && position != GetActiveIndex())
        {
            expandableTextView.collapse();
        }

        return convertView;
    }

    public void Update()
    {
        notifyDataSetChanged();
    }

    protected View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    protected int GetTitleColor(String poemTitle)
    {
        if(SaveLoadData.LoadExistPoem(TypePoem.Know, poemTitle))
        {
            return getContext().getResources().getColor(R.color.colorChapterTitleBlockKnow);
        }
        else if(SaveLoadData.LoadExistPoem(TypePoem.Learn, poemTitle))
        {
            return getContext().getResources().getColor(R.color.colorChapterTitleBlockLearn);
        }
        else
        {
            return getContext().getResources().getColor(R.color.colorLyricsTitle);
        }
    }

    public String GetActivityTitle()
    {
        return getItem(GetActiveIndex()).GetTitle();
        //return JsonManager.all.get(activeIndex).GetTitle();
    }

    public LyricsBlock GetActivityBlock()
    {
        return getItem(GetActiveIndex());
        //return JsonManager.all.get(activeIndex);
    }

    public int GetActiveIndex()
    {
        return activeIndex;
    }

    public void SetActiveIndex(int index)
    {
        activeIndex = index;
    }
}
