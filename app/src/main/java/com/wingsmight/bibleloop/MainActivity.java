package com.wingsmight.bibleloop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wingsmight.bibleloop.FadePopupBack.BackgroundBlurPopupWindow;

import java.io.File;


public class MainActivity extends AppCompatActivity implements ChapterList.OnFragmentInteractionListener, LearnList.OnFragmentInteractionListener, KnowList.OnFragmentInteractionListener,
        BillingProcessor.IBillingHandler, GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener
{
    private MediaPlayer mediaPlayer;
    private static PoemAdapter adapters[];
    private static Context mainContext;
    private static DownloadManager downloadManager;
    private static TabLayout tabLayout;
    public static String mainPath;
    private int activeAdapter = 0;
    private Button payButton;
    private BillingProcessor bp;
    private BackgroundBlurPopupWindow payPopupWindow;
    public static boolean isPayment;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle barToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = MainActivity.this;

        //Bar Toggle
        drawerLayout = findViewById(R.id.drawerLayout);
        barToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(barToggle);
        barToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Create lyrics
        Poems.CreatePoems();

        //MediaPlayer
        View mediaView = findViewById(android.R.id.content);
        mediaPlayer = new MediaPlayer(mediaView, mainContext);

        //downloadManager
        downloadManager = new DownloadManager(this);
        mainPath = getFilesDir().toString();

        //TabSliding
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Разделы"));
        tabLayout.addTab(tabLayout.newTab().setText("Учу"));
        tabLayout.addTab(tabLayout.newTab().setText("Знаю"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        SetKnowCount(Poems.GetPoem(TypePoem.Know).size());
        SetLearnCount(Poems.GetPoem(TypePoem.Learn).size());

        //adapters
        adapters = new PoemAdapter[tabLayout.getTabCount()];

        final ViewPager viewPager = findViewById(R.id.pager);
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activeAdapter = tab.getPosition();
                viewPager.setCurrentItem(activeAdapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
                int tabPosition = tab.getPosition();
                if(MainActivity.GetAdapter(tabPosition) != null && MainActivity.GetAdapter(tabPosition).GetActiveIndex() != -1)
                {
                    MainActivity.GetAdapter(tabPosition).SetActiveIndex(-1);
                    MainActivity.UpdateAdapter(tabPosition);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //Purchase
        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgUeplsbdYbt7Ji4+WrMCjdU05astNgxb/1DauoDlQ5s7B16PblLZkvveliu8h5W/qcdM2hBEUOHuv0XD50rTPyrlTvqu6tKyhEVWTmXrJntnu14qQ8Q090i8rdQYjCzo8Mo5SvxEFR8d3pNuYsZa/E0frkHrufX0EI04UYGuX9lI7gsKjJyBP2WBj5mMU1eGrzD9J944TttrE/VbbU8cFWgBmQP/hXLV9jEWoCNz076sFw/cuGPo/Oc0iamOGTSMPqJWTF0nvN38dfc/Ibzqc1FCy/DCoYF/NSTEu374iK4saceCGxotGUFeWQXd/yn0ogfgC3ANC8T8Y7lPbPaVjwIDAQAB", this);
        isPayment = SaveLoadData.LoadIsPayment();

        //auth
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {

                }
                else
                {

                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!isPayment)
                {
                    isPayment = ShowIsPayment(dataSnapshot);
                    if(isPayment)
                    {
                        UpdateAdapters();
                        DownloadPoemsForOldUsers(dataSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error)
            {

            }
        });
    }

    private void DownloadPoemsForOldUsers(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            FirebaseUser user = mAuth.getCurrentUser();
            String userID = user.getUid();

            Iterable<DataSnapshot> allPoems = ds.child(userID).child("poems").getChildren();
            for(DataSnapshot poem : allPoems)
            {
                String poemTitle = poem.getKey();
                if(!SaveLoadData.LoadExistPoem(TypePoem.Downloaded, poemTitle))
                {
                    String fileName = FileManager.PoemTitleToFileName(poemTitle);

                    downloadManager.DownloadPoem(fileName, ".mp3", Poems.GetPoemByTitle(poemTitle), null);
                }
            }
        }
    }

    private boolean ShowIsPayment(DataSnapshot dataSnapshot)
    {
        for(DataSnapshot ds : dataSnapshot.getChildren())
        {
            FirebaseUser user = mAuth.getCurrentUser();
            if(user == null)
            {
                return false;
            }
            else
            {
                String userID = user.getUid();
                if(!ds.child(userID).hasChild(("isPayment")))
                {
                    return false;
                }
                else
                {
                    return (boolean) ds.child(userID).child("isPayment").getValue();
                }
            }
        }
        return false;
    }

    public void AddPoemToDataBase(String poemName)
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            String userID = user.getUid();
            myRef.child("users").child(userID).child("poems").child(poemName).setValue(true);
        }
    }


    private void SetPurchaseToButton(Button button)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(MainActivity.this,"fullversion");
            }
        });
    }

    public static void SetAdapter(PoemAdapter adapter, int index)
    {
        MainActivity.adapters[index] = adapter;
    }

    public static PoemAdapter GetAdapter(int index)
    {
        return  adapters[index];
    }

    public static void UpdateAdapter(int index)
    {
        if(MainActivity.adapters[index] != null)
        {
            MainActivity.adapters[index].Update();
        }
    }

    public static void UpdateAdapters()
    {
        for(int i = 0; i < adapters.length; i++)
        {
            if(MainActivity.adapters[i] != null)
            {
                MainActivity.adapters[i].Update();
            }
        }
    }

    public static void SetLearnCount(int count)
    {
        tabLayout.getTabAt(1).setText("Учу" + " (" + count + ")");
    }

    public static void SetKnowCount(int count)
    {
        tabLayout.getTabAt(2).setText("Знаю" + " (" + count + ")");
    }

    public void MainPlayerPlay(View view)
    {
        if(adapters[activeAdapter].GetActiveIndex() == -1)
        {
            return;
        }

        if (!mediaPlayer.IsPlaying())
        {
            mediaPlayer.ResumeCurMusic();

            mediaPlayer.playBtn.setBackgroundResource(R.drawable.pause);
        } else
        {
            mediaPlayer.PauseCurMusic();

            mediaPlayer.playBtn.setBackgroundResource(R.drawable.play);
        }
    }

    public static Context GetContext()
    {
        return mainContext;
    }

    public void OnSetLooping5(View view)
    {
        SetLooping(view, 5);
    }
    public void OnSetLooping20(View view)
    {
        SetLooping(view, 20);
    }
    public void OnSetLooping50(View view)
    {
        SetLooping(view, 50);
    }

    private void SetLooping(View view, int count)
    {
        boolean isExist = new File(adapters[activeAdapter].GetActivityBlock().GetInternalPath(mainContext)).exists();
        if(isExist)
        {
            if(!SaveLoadData.LoadExistPoem(TypePoem.Downloaded, adapters[activeAdapter].GetActivityTitle()))
            {
                SaveLoadData.SetSaveExistPoem(TypePoem.Downloaded, adapters[activeAdapter].GetActivityTitle(), true);
                AddPoemToDataBase(adapters[activeAdapter].GetActivityTitle());

                if(!Poems.IsExist(TypePoem.Downloaded, adapters[activeAdapter].GetActivityTitle()))
                {
                    Poems.AddPoem(TypePoem.Downloaded, adapters[activeAdapter].GetActivityBlock());
                }
                if(!Poems.IsExist(TypePoem.Learn, adapters[activeAdapter].GetActivityTitle()) && !Poems.IsExist(TypePoem.Know, adapters[activeAdapter].GetActivityTitle()))
                {
                    Poems.AddPoem(TypePoem.Learn, adapters[activeAdapter].GetActivityBlock());
                }

                MainActivity.SetLearnCount(Poems.GetPoem(TypePoem.Learn).size());


                MainActivity.UpdateAdapters();
            }
        }
        else
        {
            OnDownloadFile(view);
        }

        if(isExist)
        {
            if(count == 5)
            {
                if(adapters[activeAdapter].GetActivityBlock().GetIndexInChapter() > adapters[activeAdapter].GetActivityBlock().GetChapterSize() / 2 && !adapters[activeAdapter].GetActivityBlock().GetChapter().equals("БПУФ (Библейские принципы управления финансами)") && !isPayment)
                {
                    Pay();
                }
                else
                {
                    mediaPlayer.ChangeSong(adapters[activeAdapter].GetActivityBlock().GetInternalPath(mainContext), count);
                }
            }
            else
            {
                if(isPayment)
                {
                    mediaPlayer.ChangeSong(adapters[activeAdapter].GetActivityBlock().GetInternalPath(mainContext), count);
                }
                else
                {
                    Pay();
                }
            }
        }
    }

    private void Pay()
    {
        LinearLayout mRelativeLayout = findViewById(R.id.mainActivity);
        LayoutInflater inflater = (LayoutInflater) mainContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pay_popup_window,null);

        payPopupWindow = new BackgroundBlurPopupWindow(
                popupView,
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                this,
                true
        );


        payButton = popupView.findViewById(R.id.pay);
        Button closeButton = popupView.findViewById(R.id.closeButton);


        ScrollingMovementMethod scrollingMovementMethod = new ScrollingMovementMethod();

        TextView textView = popupView.findViewById(R.id.tv);
        textView.setMovementMethod(scrollingMovementMethod);

        SpannableString ss0 =  new SpannableString(getResources().getString(R.string.textToPay0));
        ss0.setSpan(new RelativeSizeSpan(1.3f), 0, getResources().getString(R.string.textToPay0).length(), 0); // set size
        ss0.setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getString(R.string.textToPay0).length(), 0); // set size
        ss0.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorChapterTitleBlockLearn)), 0, ss0.length(), 0);// set color

        SpannableString ss1 =  new SpannableString(getResources().getString(R.string.textToPay1));
        ss1.setSpan(new RelativeSizeSpan(1.1f), 0, getResources().getString(R.string.textToPay1).length(), 0); // set size
        ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getString(R.string.textToPay1).length(), 0); // set size

        SpannableString ss2 =  new SpannableString(getResources().getString(R.string.textToPay2));

        SpannableString textToPayAll =  new SpannableString(getResources().getString(R.string.textToPayAll));
        textToPayAll.setSpan(new RelativeSizeSpan(1.1f), 0, getResources().getString(R.string.textToPayAll).length(), 0); // set size
        textToPayAll.setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getString(R.string.textToPayAll).length(), 0); // set size

        SpannableString ss3 =  new SpannableString(getResources().getString(R.string.textToPay3));

        SpannableString textToPayAllThem =  new SpannableString(getResources().getString(R.string.textToPayAllThem));
        textToPayAllThem.setSpan(new RelativeSizeSpan(1.1f), 0, getResources().getString(R.string.textToPayAllThem).length(), 0); // set size
        textToPayAllThem.setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getString(R.string.textToPayAllThem).length(), 0); // set size

        SpannableString ss4 =  new SpannableString(getResources().getString(R.string.textToPay4));

        SpannableString ss5 =  new SpannableString(getResources().getString(R.string.textToPay5));
        ss5.setSpan(new RelativeSizeSpan(1.1f), 0, getResources().getString(R.string.textToPay5).length(), 0); // set size
        ss5.setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getString(R.string.textToPay5).length(), 0); // set size

        SpannableString ss6 =  new SpannableString(getResources().getString(R.string.textToPay6));

        TextView textToPay0 = popupView.findViewById(R.id.textToPay0);
        textToPay0.setMovementMethod(scrollingMovementMethod);
        textToPay0.setText(ss0);

        CharSequence finalText = TextUtils.concat(ss1, ss2, textToPayAll, ss3, textToPayAllThem, ss4, ss5, ss6);
        textView.setText(finalText);


        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                payPopupWindow.dismiss();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                payPopupWindow.dismiss();
            }
        });
        SetPurchaseToButton(payButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            payPopupWindow.setElevation(20);
        }

        payPopupWindow.setBlurRadius(10);
        payPopupWindow.setDownScaleFactor(1.2f);
        payPopupWindow.setDarkColor(Color.parseColor("#a0000000"));
        payPopupWindow.resetDarkPosition();
        payPopupWindow.darkFillScreen();

        payPopupWindow.setFocusable(true);
        payPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        payPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        payPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(barToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void OnDownloadFile(View view)
    {
        String poemTitle = adapters[activeAdapter].GetActivityTitle();
        poemTitle = FileManager.PoemTitleToFileName(poemTitle);

        view.setClickable(false);
        downloadManager.DownloadPoem(poemTitle, ".mp3", adapters[activeAdapter].GetActivityBlock(), view);
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details)
    {
        isPayment = true;
        SaveLoadData.SaveIsPayment(true);
        payPopupWindow.dismiss();
        UpdateAdapters();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            String userID = user.getUid();
            myRef.child("users").child(userID).child("isPayment").setValue(true);
        }
    }

    @Override
    public void onPurchaseHistoryRestored()
    {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error)
    {

    }

    @Override
    public void onBillingInitialized()
    {

    }

    @Override
    public void onDestroy()
    {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            SetSignInUser(user.getUid());
                        }
                        else
                        {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void SetSignInUser(String userID)
    {
        myRef.child("users").child(userID).child("isSignIn").setValue(false);
        myRef.child("users").child(userID).child("isSignIn").setValue(true);

        myRef.child("users").child(userID).child("isPayment").setValue(isPayment);

        if (Poems.GetPoem(TypePoem.Downloaded) != null)
        {
            for(LyricsBlock downloadPoem : Poems.GetPoem(TypePoem.Downloaded))
            {
                myRef.child("users").child(userID).child("poems").child(downloadPoem.GetTitle()).setValue(true);
            }
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    private void updateUI(FirebaseUser user)
    {
        if (user != null)
        {
            NavigationView navigationView = findViewById(R.id.nav_view);

            TextView userMail = navigationView.getHeaderView(0).findViewById(R.id.userMail);
            userMail.setText(user.getEmail());

            TextView userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
            userName.setText(user.getDisplayName());

            if(user.getPhotoUrl() != null)
            {
                Uri photoUri = user.getPhotoUrl();
                ImageView userImage = navigationView.getHeaderView(0).findViewById(R.id.userImage);

                Glide.with(this).load(photoUri.toString()).into(userImage);
            }

            MenuItem search = navigationView.getMenu().findItem(R.id.LogIn);
            search.setVisible(false);

            MenuItem logOut = navigationView.getMenu().findItem(R.id.LogOut);
            logOut.setVisible(true);
        }
        else
        {
            NavigationView navigationView = findViewById(R.id.nav_view);

            TextView userMail = navigationView.getHeaderView(0).findViewById(R.id.userMail);
            userMail.setText("");

            TextView userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
            userName.setText("");

            ImageView userImage = navigationView.getHeaderView(0).findViewById(R.id.userImage);
            userImage.setImageDrawable(getResources().getDrawable(R.drawable.sign_avatar));

            MenuItem search = navigationView.getMenu().findItem(R.id.LogOut);
            search.setVisible(false);

            MenuItem logIn = navigationView.getMenu().findItem(R.id.LogIn);
            logIn.setVisible(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.LogIn)
        {
            signIn();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
        else if (id == R.id.LogOut)
        {
            signOut();
            updateUI(null);
        }
        else if (id == R.id.Rate)
        {
            Rate();
        }
        else if (id == R.id.PrivatePolitic)
        {
            SendToPP();
        }

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut()
    {
        FirebaseAuth.getInstance().signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>()
        {
            @Override
            public void onResult(@NonNull Status status)
            {
                updateUI(null);
            }
        });
    }

    private void Rate()
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.wingsmight.bibleloop"));
        startActivity(browserIntent);
    }

    private void SendToPP()
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wingsmight.github.io/bibleloop/"));
        startActivity(browserIntent);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }
}
