package whatisnext.ritesh.com.whatisnext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class PracticeScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech engine;
    private static final int REQ_CODE_SPEECH_INPUT = 987;
    int ind = 0;
    ArrayList<String> list;
    String currentWord = "";
    TextView mWord,mResult;
    private Button mButton1,mButton2;

    public TextView getWord() {
        if(mWord == null){
            mWord = (TextView) findViewById(R.id.word);
        }
        return mWord;
    }
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            engine.setLanguage(Locale.US);
        }
    }
    private void speech() {
        engine.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null);
    }
    public TextView getResult() {
        if(mResult == null){
            mResult = (TextView) findViewById(R.id.result_msg);
        }
        return mResult;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_screen);
        createDictionary();
        engine = new TextToSpeech(this, this);
        getButton1().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        getButton2().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewWord();
            }
        });
        getNewWord();
        Button speechButton = (Button) findViewById(R.id.speechButton);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech();
            }
        });
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    double score = getScorePercentage(currentWord,result.get(0));
                    score = Math.round(score*100);
                    score /= 100;
                    if(score == 100D){
                        congratulations();
                    }else{
                        tryAgainOrSkip(result.get(0));
                    }
                }
                break;
            }

        }
    }


    private void tryAgainOrSkip( String input) {
        String str = "We heard you say '" + input + "'. Please Try Again.";
        getResult().setText(str);
        getButton1().setText("Try Again");
    }

    private void congratulations() {
        String str = "Congratulations for the word '" + currentWord + "'. Now try Next One.";
        getResult().setText(str);
        getNewWord(true);
    }


    public double getScorePercentage(String actualAnswer, String observedString){
        if(observedString == null)
            return 0;
        observedString = observedString.toLowerCase().replaceAll("[^a-z ]","");
        actualAnswer = actualAnswer.toLowerCase().replaceAll("[^a-z ]","");
//        Log.e("",observedString + " &&& " + actualAnswer);
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
    void getNewWord(){
        getResult().setText("");
        getNewWord(true);
    }
    void getNewWord(boolean dontchange){
        getButton1().setText("Try it");
        if(list == null || ind >= list.size()){
            list = getRandomWords(100);
            ind = 0;
        }

        currentWord = list.get(ind);
        getWord().setText(currentWord);
        ind++;
    }
    private ArrayList<String> dictionaryList;
    private void createDictionary(){
        dictionaryList = new ArrayList<String>();

        BufferedReader dict = null; //Holds the dictionary file
        AssetManager am = this.getAssets();

        try {
            //dictionary.txt should be in the assets folder.
            dict = new BufferedReader(new InputStreamReader(am.open("dictionary.txt")));

            String word;
            while((word = dict.readLine()) != null){
                if(word.length() > 4){
                    dictionaryList.add(word);
                    Log.e("",word);
                }
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Precondition: the dictionary has been created.
    private ArrayList<String> getRandomWords(int n){
        if(n > dictionaryList.size()){
            n = dictionaryList.size();
        }
        ArrayList<String> randoms = new ArrayList<>();
        while(n > 0){
            int rd = (int)(Math.random() * n );
            randoms.add(dictionaryList.get(rd));
            String temp = dictionaryList.get(rd);
            dictionaryList.set(rd,dictionaryList.get(n-1));
            dictionaryList.set(n-1,temp);
            n--;
        }
        return randoms;
    }

}
