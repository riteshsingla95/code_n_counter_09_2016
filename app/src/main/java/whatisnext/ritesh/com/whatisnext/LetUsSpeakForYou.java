package whatisnext.ritesh.com.whatisnext;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class LetUsSpeakForYou extends ActionBarActivity implements TextToSpeech.OnInitListener {
    TextToSpeech engine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_let_us_speak_for_you);
        engine = new TextToSpeech(this, this);
        Button speechButton = (Button) findViewById(R.id.speechButton);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech();
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            engine.setLanguage(Locale.US);
        }
    }
    private void speech() {
        EditText editText = (EditText) findViewById(R.id.editText);
        engine.speak(editText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }
}
