package com.wingsmight.bibleloop;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.NonNull;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class DownloadManager
{
    private StorageReference mStorageRef;
    private Context mainContext;
    private MainActivity mainActivity;

    public DownloadManager(MainActivity mainActivity)
    {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mainContext = MainActivity.GetContext();
        this.mainActivity = mainActivity;
    }

    private class DownloadInfo
    {
        public String downloadFileName;
        public String downloadExtensionWithDot;
        public LyricsBlock downloadPoem;
        public View buttonView;

        public DownloadInfo(String fileName, String ExtensionWithDot, LyricsBlock downloadPoem, View buttonView)
        {
            this.downloadFileName = fileName;
            this.downloadExtensionWithDot = ExtensionWithDot;
            this.downloadPoem = downloadPoem;
            this.buttonView = buttonView;
        }
    }

    private class Downloader
    {
        private StorageReference ref;
        private Context context;
        private DownloadInfo downloadInfo;
        private long downloadId;

        public Downloader(StorageReference ref, DownloadInfo downloadInfo, Context context)
        {
            this.ref = ref;
            this.context = context;
            this.downloadInfo = downloadInfo;
        }

        public void DownloadFile()
        {
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri uri)
                {
                    String url = uri.toString();

                    DownloadPoem(url);
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(mainContext, "Аудио отсутствует на сервере", Toast.LENGTH_SHORT).show();

                    if(downloadInfo.buttonView != null)
                    {
                        downloadInfo.buttonView.setClickable(true);
                    }
                }
            });
        }

        private void DownloadPoem(String url)
        {
            String destinationDirectory = DIRECTORY_DOWNLOADS;




//            ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
//            progressBar.getProgressDrawable().setColorFilter(mainContext.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
//            progressBar.setProgress(0);

            Poems.AddPoem(TypePoem.StartDownLoaded, downloadInfo.downloadPoem);
            MainActivity.UpdateAdapters();




            android.app.DownloadManager downloadManager = (android.app.DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(uri);

            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalFilesDir(context, destinationDirectory, downloadInfo.downloadFileName + downloadInfo.downloadExtensionWithDot);
            request.setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_WIFI | android.app.DownloadManager.Request.NETWORK_MOBILE);
            request.setVisibleInDownloadsUi(false);
            this.downloadId = downloadManager.enqueue(request);

            ShowProgressBar(downloadManager);
        }

        private void ShowProgressBar(final android.app.DownloadManager manager)
        {
//            final ProgressBar progressBar = mainActivity.findViewById(R.id.progressBar);
//            progressBar.setProgress(5);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    boolean IsDownloading = true;

                    while (IsDownloading) {
                        android.app.DownloadManager.Query q = new android.app.DownloadManager.Query();
                        q.setFilterById(downloadId);

                        Cursor cursor = manager.query(q);
                        cursor.moveToFirst();
//                        int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                        int bytes_total = cursor.getInt(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//
//                        final double dl_progress = ((int) ((bytes_downloaded * 100l) / bytes_total) / 1.32 + 5);
//                        progressBar.setProgress((int) dl_progress);

                        if (cursor.getInt(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS)) == android.app.DownloadManager.STATUS_SUCCESSFUL) {
                            IsDownloading = false;
                        }

                        cursor.close();
                    }

                    String downloadPath = mainContext.getExternalFilesDir("Download").toString();
                    File downloadedFile = new File(downloadPath, downloadInfo.downloadFileName + downloadInfo.downloadExtensionWithDot);
                    final String internalPath = mainContext.getFilesDir().toString();

                    try
                    {
                        FileManager.CopyFile(downloadedFile, new File(internalPath, downloadInfo.downloadFileName + downloadInfo.downloadExtensionWithDot));

//                        progressBar.setProgress(88);

                        FileManager.DeleteFile(downloadedFile);

//                        progressBar.setProgress(98);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Poems.RemovePoem(TypePoem.StartDownLoaded, downloadInfo.downloadPoem);
                    Poems.AddPoem(TypePoem.Downloaded, downloadInfo.downloadPoem);
                    Poems.AddPoem(TypePoem.Learn, downloadInfo.downloadPoem);
//                    progressBar.setProgress(100);

                    mainActivity.AddPoemToDataBase(downloadInfo.downloadPoem.GetTitle());

                    mainActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            MainActivity.UpdateAdapters();

//                            progressBar.setProgress(0);
//                            progressBar.getProgressDrawable().setColorFilter(mainContext.getResources().getColor(R.color.colorMainBackground), android.graphics.PorterDuff.Mode.SRC_IN);

                            MainActivity.SetLearnCount(Poems.GetPoem(TypePoem.Learn).size());
                        }
                    });

                    if(downloadInfo.buttonView != null)
                    {
                        downloadInfo.buttonView.setClickable(true);
                    }
                }
            }).start();
        }
    }


    public void DownloadPoem(String fileName, String extensionWithDot, LyricsBlock poem, View buttonView)
    {
        //Check network
        ConnectivityManager conMgr = (ConnectivityManager)mainContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null)
        {
            Toast.makeText(mainContext, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show();

            if(buttonView != null)
            {
                buttonView.setClickable(true);
            }
            return;
        }

        DownloadInfo downloadInfo = new DownloadInfo(fileName, extensionWithDot, poem, buttonView);
        StorageReference ref = mStorageRef.child(downloadInfo.downloadFileName + downloadInfo.downloadExtensionWithDot);

        Downloader downloader = new Downloader(ref, downloadInfo, mainContext);
        downloader.DownloadFile();
    }
}
