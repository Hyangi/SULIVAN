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

/**
 * Created by Master on 2015-11-10.
 */
public class Menu extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;

    boolean typmod = false;

    boolean isfirst = true;

    boolean isthatinmenu = false;
    InputMethodManager mgr;

    private EditText voicedata;

    String nowTitle, nowTitleHangle;
    String[] nowmenu;
    String[] nowmenuvoice;

    String[] mp3name = {"menushort","anothermenu","modvoice","modkeyboardshort","menuwrong","internetnot","notstt","repeatiscenter"};
    private int mp3res,datares;

    MediaPlayer music;

    ConnectivityManager manager;
    NetworkInfo mobile;
    NetworkInfo wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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

        Intent intent = getIntent();
        nowTitle = intent.getStringExtra("title");
        nowTitleHangle = intent.getStringExtra("titlehangle");

        setTitle("메인화면 > " + nowTitleHangle + " > 메뉴");

        MyApplication myApp = (MyApplication) getApplication();
        typmod = myApp.getState();

        // 커스터머 보이스파일 이름 불러오기
        datares = getResources().getIdentifier(nowTitle + "menu", "array", getPackageName());
        nowmenu = getResources().getStringArray(datares);
        datares = getResources().getIdentifier(nowTitle, "array", getPackageName());
        nowmenuvoice = getResources().getStringArray(datares);

        voicedata.setOnEditorActionListener(new TextView.OnEditorActionListener() { //키보드 입력 모드일시시
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String reslt = voicedata.getText().toString();
                    for (int i=0; i<nowmenu.length; i++) {
                        if (reslt.equals(nowmenu[i])) {
                            isthatinmenu = true;
                            mp3res = getResources().getIdentifier(nowmenuvoice[i], "raw", getPackageName());
                            music = MediaPlayer.create(getApplicationContext(), mp3res);
                            music.setLooping(false);
                            music.start();
                            break;
                        }
                    }
                    if(!isthatinmenu){
                        mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName());
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp3res = getResources().getIdentifier(nowTitle + "topmenu", "raw", getPackageName());
                                music = MediaPlayer.create(getApplicationContext(), mp3res);
                                music.setLooping(false);
                                music.start();
                                music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp3res = getResources().getIdentifier(mp3name[1], "raw", getPackageName());
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
                        });
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

                isfirst = true;
                try {
                    music.stop();
                    music.release();
                }catch (Exception e){

                }

                if(typmod){
                    voicedata.setInputType(1);
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
                    } catch (ActivityNotFoundException a) {     //STT미지원
                        MyApplication myApp = (MyApplication) getApplication();
                        myApp.setState(true);
                        typmod = myApp.getState();

                        voicedata.setInputType(1);
                        mp3res = getResources().getIdentifier(mp3name[6], "raw", getPackageName());
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                    }
                }
            }
        });
        mp3res = getResources().getIdentifier(mp3name[0], "raw", getPackageName());
        music = MediaPlayer.create(this, mp3res);
        music.setLooping(false);
        music.start();
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp3res = getResources().getIdentifier(nowTitle+"topmenu", "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
                music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp3res = getResources().getIdentifier(mp3name[1], "raw", getPackageName());
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
        });
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
                    String voicereslt = voicedata.getText().toString();
                    for (int i=0; i<nowmenu.length; i++) {
                        if (voicereslt.equals(nowmenu[i])) {
                            isthatinmenu = true;
                            mp3res = getResources().getIdentifier(nowmenuvoice[i], "raw", getPackageName());
                            music = MediaPlayer.create(this, mp3res);
                            music.setLooping(false);
                            music.start();
                            break;
                        }
                    }
                    if(!isthatinmenu){
                        mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName());
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp3res = getResources().getIdentifier(nowTitle + "topmenu", "raw", getPackageName());
                                music = MediaPlayer.create(getApplicationContext(), mp3res);
                                music.setLooping(false);
                                music.start();
                                music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp3res = getResources().getIdentifier(mp3name[1], "raw", getPackageName());
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
                        });
                    }
                }
                break;
            }

        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(isfirst == false){
            mp3res = getResources().getIdentifier("menushort", "raw", getPackageName());
            music = MediaPlayer.create(getApplicationContext(), mp3res);
            music.setLooping(false);
            music.start();
        }
        isfirst = false;
    }
    public void repeat(View view) {
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        }catch (Exception e){

        }
        mp3res = getResources().getIdentifier(nowTitle+"topmenu", "raw", getPackageName());
        music = MediaPlayer.create(getApplicationContext(), mp3res);
        music.setLooping(false);
        music.start();
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp3res = getResources().getIdentifier(mp3name[1], "raw", getPackageName());
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            music.stop();
            music.release();
        }catch (Exception e){

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
            if (mobile.isConnected() || wifi.isConnected()) {
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setState(false);
                typmod = myApp.getState();

                voicedata.setInputType(0);
                mp3res = getResources().getIdentifier(mp3name[2], "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
            }else{
                mp3res = getResources().getIdentifier(mp3name[5], "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
            }
        }
        else{
            MyApplication myApp = (MyApplication) getApplication();
            myApp.setState(true);
            typmod = myApp.getState();

            voicedata.setInputType(1);
            mp3res = getResources().getIdentifier(mp3name[3], "raw", getPackageName());
            music = MediaPlayer.create(getApplicationContext(), mp3res);
            music.setLooping(false);
            music.start();
        }
    }
}
