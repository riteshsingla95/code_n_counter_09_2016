package whatisnext.ritesh.com.whatisnext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        String userId = getUserId();
        if(userId == null) {
            String android_id = Secure.getString(getContentResolver(),
                    Secure.ANDROID_ID);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("user_id", android_id);
            editor.commit();
        }

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListenScreen();
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPractice();
            }
        });
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startArena();
            }
        });


//        final ImageView mImageView;
//        String url = "http://i.imgur.com/7spzG.png";
//        mImageView = (ImageView) findViewById(R.id.image);
//    // Retrieves an image specified by the URL, displays it in the UI.
//        ImageRequest request = new ImageRequest(url,
//
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap bitmap) {
//                        mImageView.setImageBitmap(bitmap);
//                    }
//                }, 0, 0, null,
//                new Response.ErrorListener() {
//                    public void onErrorResponse(VolleyError error) {
//                        mImageView.setImageResource(R.drawable.notification_template_icon_bg);
//                    }
//                });
//    // Access the RequestQueue through your singleton class.
//        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void startArena() {
        Intent intent = new Intent(this, PlayScreen.class);
        startActivity(intent);
    }

    private void startPractice() {
        Intent intent = new Intent(this, PracticeScreen.class);
        startActivity(intent);
    }

    private void startListenScreen() {
        Intent intent = new Intent(this, LetUsSpeakForYou.class);
        startActivity(intent);
    }


    String getUserId(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString("user_id", null);
    }
//    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//    SharedPreferences.Editor editor = settings.edit();
//    editor.putString("language", language);
//    editor.commit();
//    Read:
// SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//    SharedPreferences.Editor editor = settings.edit();
//    editor.putString("language", language);
//    editor.commit();
//    Read:


}
