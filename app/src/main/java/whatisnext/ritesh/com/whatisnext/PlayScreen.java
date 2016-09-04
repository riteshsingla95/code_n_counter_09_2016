package whatisnext.ritesh.com.whatisnext;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Locale;

public class PlayScreen extends ActionBarActivity {

    SharedPreferences settings;
    private static final String HOST_URL = "10.177.12.110";
    private static final int REQ_CODE_SPEECH_INPUT = 999;
    private static final int TYPE_WIFI = 0;
    private static final int TYPE_MOBILE = 1;
    private static final int TYPE_NOT_CONNECTED = 2;

    private int quizStatus = 0;
    private Snackbar snackbar;
    private String mFinalUrl, mText;
    private CoordinatorLayout coordinatorLayout;

    public String getUserId() {
        if(userId == null){
            userId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return userId;
    }

    String userId;

    private boolean internetConnected=true;
    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }
    /**
     *  Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };
    private void setSnackbarMessage(String status,boolean showBar) {
        String internetStatus="";
        if(status.equalsIgnoreCase("Wifi enabled")||status.equalsIgnoreCase("Mobile data enabled")){
            internetStatus="Internet Connected";
        }else {
            internetStatus="Lost Internet Connection";
        }
        snackbar = Snackbar
                .make(coordinatorLayout, internetStatus, Snackbar.LENGTH_LONG)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if(internetStatus.equalsIgnoreCase("Lost Internet Connection")){
            if(internetConnected){
                snackbar.show();
                internetConnected=false;
            }
        }else{
            if(!internetConnected){
                internetConnected=true;
                snackbar.show();
            }
        }
    }
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("What Is Next");
        toolbar.setTitleTextColor(Color.WHITE);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorlayout);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        startNewQuestion();

        getButton1().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase("speechTried");
                promptSpeechInput();
            }
        });
        getButton2().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quizStatus == 0) {
                    increase("skipped");
                }
                startNewQuestion();
            }
        });
        getButton3().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                increase("hintsSeen");
                getButton3().setVisibility(View.INVISIBLE);
                getAnswer().setText(mText);
                getAnswer().setVisibility(View.VISIBLE);
            }
        });
//        Log.e("",String.valueOf(getScorePercentage("scac","SCAC")));
//        Log.e("",String.valueOf(getScorePercentage("scac","S...;'..;'CAC")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_screen, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_nerd_stats) {
            Intent intent = new Intent(this,NerdStat.class);
            startActivity(intent);
            return true;
        }
//        if (id == R.id.action_share) {
//           // dialPhoneNumber(getIntent().getStringExtra(ContactsActivity.CONTACT_ID));
//
//            // Get the provider and hold onto it to set/change the share intent.
////            ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
//
////            Adapter adapter1 = listView.getAdapter();
//
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    void startNewQuestion(){
        quizStatus = 0;
        getAnswer().setVisibility(View.INVISIBLE);
        getButton3().setVisibility(View.VISIBLE);
        getButton1().setVisibility(View.VISIBLE);
        getButton1().setText("Try It");
        getButton2().setText("Skip");
        getResultTextView().setText("");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = String.format("http://" + HOST_URL + "/generateRandomVideo.php?userid=%1$s",getUserId());

        Log.e("url",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        String details[] = response.split(",");
                        mFinalUrl = details[2];
                        mText = details[1];
                        playVideo(details[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"That didn't work!",Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }



    void playVideo(String mute_url){
        if(getVideo().isPlaying()){
            getVideo().stopPlayback();
        }
        Uri uri=Uri.parse(mute_url);
        getVideo().setVideoURI(uri);
        getVideo().start();
        getVideo().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!getVideo().isPlaying()){
                    getVideo().seekTo(0);
                    getVideo().start();
                }
                return true;
            }
        });
    }
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"SPEECH NOT SUPPORTED",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    double score = getScorePercentage(mText,result.get(0));
                    score = Math.round(score*100);
                    score /= 100;
                    if(score == 100D){
                        congratulations();
                    }else{
                        tryAgainOrSkip(score,result.get(0));
                    }
                }
                break;
            }

        }
    }

    private void tryAgainOrSkip(double score, String input) {
        String str = "We heard you say '" + input + "'. Your attempt was " + String.valueOf(score) + "% match. Please Try Again.";
        getResultTextView().setText(str);
        getButton1().setText("Try Again");
    }

    private void congratulations() {
        quizStatus = 1;
        getButton1().setVisibility(View.INVISIBLE);
        String str = "Congratulations. You did it.";
        getResultTextView().setText(str);
        getButton2().setText("Next");
        if(mFinalUrl != null)
            playVideo(mFinalUrl);
        //add one to completed.
        //Like/Disklike
        increase("completed");
    }


    void increase(String s){

        int completed = settings.getInt(s, 0);
        completed++;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(s, completed);
        editor.commit();
    }
    /**
     * returns the score from matching.
     */
    public double getScorePercentage(String actualAnswer, String observedString){
        if(observedString == null)
            return 0;
        observedString = observedString.toLowerCase().replaceAll("[^a-z ]","");
        actualAnswer = actualAnswer.toLowerCase().replaceAll("[^a-z ]","");
        Log.e("",observedString + " &&& " + actualAnswer);
        String[] observedStringArray = observedString.split(" ");
        String[] actualAnswerArray = actualAnswer.split(" ");
        int correctWords = actualAnswerArray.length - Math.abs(actualAnswerArray.length - observedStringArray.length);
        for(int i = 0 ; i < actualAnswerArray.length && i < observedStringArray.length; i++){
            if(!actualAnswerArray[i].equals(observedStringArray[i])){
                correctWords--;
            }
        }
        if(correctWords < 0)
            correctWords = 0;
        double ans = correctWords*100;
        ans /= actualAnswerArray.length;
        return ans;
    }
    TextView mResultTextView, mAnswer;
    Button mButton1, mButton2, mButton3;
    VideoView mVideo;

    public VideoView getVideo(){
        if(mVideo == null){
            mVideo = (VideoView)findViewById(R.id.video_view);
        }
        return mVideo;
    }

    public TextView getResultTextView() {
        if(mResultTextView == null){
            mResultTextView = (TextView) findViewById(R.id.result_msg);
        }
        return mResultTextView;
    }
    public TextView getAnswer() {
        if(mAnswer == null){
            mAnswer = (TextView) findViewById(R.id.answer);
        }
        return mAnswer;
    }
    public Button getButton1() {
        if(mButton1 == null){
            mButton1 = (Button) findViewById(R.id.button1);
        }
        return mButton1;
    }
    public Button getButton2() {
        if(mButton2 == null){
            mButton2 = (Button) findViewById(R.id.button2);
        }
        return mButton2;
    }
    public Button getButton3() {
        if(mButton3 == null){
            mButton3 = (Button) findViewById(R.id.button3);
        }
        return mButton3;
    }
}
