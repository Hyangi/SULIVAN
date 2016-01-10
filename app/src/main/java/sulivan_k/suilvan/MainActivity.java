package sulivan_k.suilvan;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;

    boolean typmod = false;
    boolean isfirst = true;

    EditText voicedata;
    InputMethodManager mgr;
    MediaPlayer music;


    String[] union;
    String[] unionalphabetname;
    String[] mp3name = {"mainshort","main","mainrepeat","mainnotinunion","modvoice","modkeyboardshort","modkeyboardnot","mainwrong","internetnot","notstt"};
    int mp3res, datares;

    ConnectivityManager manager;
    NetworkInfo mobile;
    NetworkInfo wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datares = getResources().getIdentifier("union", "array", getPackageName());
        union = getResources().getStringArray(datares);
        datares = getResources().getIdentifier("unionalphabetname", "array", getPackageName());
        unionalphabetname = getResources().getStringArray(datares);

        manager =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        voicedata = (EditText) findViewById(R.id.btnSpeak);

        if (mobile.isConnected() || wifi.isConnected()){
            voicedata.setInputType(0);
        }else{
            voicedata.setInputType(1);
            MyApplication myApp = (MyApplication) getApplication();
            myApp.setState(true);
            typmod = myApp.getState();
        }

        mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        MyApplication myApp = (MyApplication) getApplication();
        typmod = myApp.getState();




        voicedata.setOnEditorActionListener(new TextView.OnEditorActionListener() { //키보드 입력 모드일시시
           @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String reslt = voicedata.getText().toString().toLowerCase();
                    for (int i=0; i<union.length; i++) {
                        if(reslt.toLowerCase().equals(union[i])){ //가맹점 확인
                            Intent intent = new Intent(getApplicationContext(), CustomerPage.class);
                            intent.putExtra("titlehangle", union[i]);
                            intent.putExtra("titlealpha", unionalphabetname[i]);
                            startActivity(intent);
                            break;
                        }
                        if(i == union.length-1){ // 가맹점이 아니면
                            mp3res = getResources().getIdentifier(mp3name[7], "raw", getPackageName());//안내메세지
                            music = MediaPlayer.create(getApplicationContext(), mp3res);
                            music.setLooping(false);
                            music.start();
                            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp3res = getResources().getIdentifier(mp3name[3], "raw", getPackageName());//안내메세지
                                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                                    music.setLooping(false);
                                    music.start();
                                    music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            voicedata.performClick();
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
                return false;
            }
        });


        voicedata.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(300);

                try {
                    music.stop();
                    music.release();
                }catch (Exception e){

                }
                if(typmod){
                    voicedata.setText("");
                    mgr.showSoftInput(voicedata, InputMethodManager.SHOW_FORCED);
                }
                else {

                    //구글 STT
                    isfirst = true;
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ko-KR");

                    try {
                        startActivityForResult(intent, RESULT_SPEECH);
                        voicedata.setText("");
                    } catch (ActivityNotFoundException a) {                         //STT 미지원
                        MyApplication myApp = (MyApplication) getApplication();
                        myApp.setState(true);
                        typmod = myApp.getState();

                        voicedata.setInputType(1);
                        mp3res = getResources().getIdentifier(mp3name[9], "raw", getPackageName());
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp3res = getResources().getIdentifier(mp3name[6], "raw", getPackageName());
                                music = MediaPlayer.create(getApplicationContext(), mp3res);
                                music.setLooping(false);
                                music.start();
                            }
                        });
                    }
                }
            }
        });

        if(isfirst) {
            mp3res = getResources().getIdentifier(mp3name[0], "raw", getPackageName());
            music = MediaPlayer.create(this, mp3res);
            music.setLooping(false);
            music.start();
            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp3res = getResources().getIdentifier(mp3name[1], "raw", getPackageName());
                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                    music.setLooping(false);
                    music.start();
                    if (mobile.isConnected() || wifi.isConnected()){
                        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if(typmod) {
                                    music = MediaPlayer.create(getApplicationContext(), R.raw.modkeyboardnot);
                                    music.setLooping(false);
                                    music.start();
                                }
                                else {
                                    mp3res = getResources().getIdentifier(mp3name[2], "raw", getPackageName());
                                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                                    music.setLooping(false);
                                    music.start();
                                    music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            voicedata.performClick();
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp3res = getResources().getIdentifier(mp3name[8], "raw", getPackageName());
                                music = MediaPlayer.create(getApplicationContext(), mp3res);
                                music.setLooping(false);
                                music.start();
                            }
                        });
                    }
                }
            });
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    voicedata.setText(text.get(0));
    //보이스 입력 후
                    final TextView voicedata = (TextView)findViewById(R.id.btnSpeak);

                    String reslt = voicedata.getText().toString().toLowerCase();
                    for (int i=0; i<union.length; i++) {
                        if(reslt.equals(union[i])){ //가맹점 확인
                            Intent intent = new Intent(this, CustomerPage.class);
                            intent.putExtra("titlehangle", union[i]);
                            intent.putExtra("titlealpha", unionalphabetname[i]);
                            startActivity(intent);
                            break;
                        }
                        if(i == union.length-1){ // 가맹점이 아니면
                            mp3res = getResources().getIdentifier(mp3name[7], "raw", getPackageName());//안내메세지
                            music = MediaPlayer.create(getApplicationContext(), mp3res);
                            music.setLooping(false);
                            music.start();
                            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp3res = getResources().getIdentifier(mp3name[3], "raw", getPackageName());//안내메세지
                                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                                    music.setLooping(false);
                                    music.start();
                                    music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            voicedata.performClick();
                                        }
                                    });
                                }
                            });
                        }
                    }


                }
                break;
            }

        }
    }

    public void repeat(View view) {
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        }catch (Exception e){

        }
        if(typmod) {
            music = MediaPlayer.create(this, R.raw.modkeyboardnot);
            music.setLooping(false);
            music.start();
        }
        else {
            music = MediaPlayer.create(this, R.raw.mainrepeat);
            music.setLooping(false);
            music.start();
            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    voicedata.performClick();
                }
            });
        }
    }
    public void modcng(View view){
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        }catch (Exception e){

        }
        if(typmod){
            manager =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mobile.isConnected() || wifi.isConnected()){
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setState(false);
                typmod = myApp.getState();

                voicedata.setInputType(0);
                mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
            }else{
                mp3res = getResources().getIdentifier(mp3name[8], "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
            }
        }
        else {
            MyApplication myApp = (MyApplication) getApplication();
            myApp.setState(true);
            typmod = myApp.getState();

            voicedata.setInputType(1);
            mp3res = getResources().getIdentifier(mp3name[5], "raw", getPackageName());
            music = MediaPlayer.create(getApplicationContext(), mp3res);
            music.setLooping(false);
            music.start();
            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp3res = getResources().getIdentifier(mp3name[6], "raw", getPackageName());
                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                    music.setLooping(false);
                    music.start();
                }
            });
        }

    }
    @Override
    public void onResume(){
        super.onResume();
        MyApplication myApp = (MyApplication) getApplication();
        typmod = myApp.getState();
        if(isfirst == false){
            mp3res = getResources().getIdentifier(mp3name[0], "raw", getPackageName());
            music = MediaPlayer.create(getApplicationContext(), mp3res);
            music.setLooping(false);
            music.start();
        }
        isfirst = false;
    }
    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        try {
            music.stop();
            music.release();
            mgr.hideSoftInputFromWindow(voicedata.getWindowToken(), 0);
        }catch (Exception e){

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            music.stop();
            music.release();
            mgr.hideSoftInputFromWindow(voicedata.getWindowToken(), 0);
        }catch (Exception e){

        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            music.stop();
            music.release();
            mgr.hideSoftInputFromWindow(voicedata.getWindowToken(), 0);
        }catch (Exception e){

        }
    }
}

