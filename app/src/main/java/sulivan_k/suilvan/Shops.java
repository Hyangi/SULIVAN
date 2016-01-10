package sulivan_k.suilvan;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Shops extends AppCompatActivity {

    boolean isfirst = true;

    private TextToSpeech myTTS;
    private TextView shopText, phoneText;

    String nowTitle, nowTitleHangle;
    String searched;

    String[] canspeak;
    String[] mp3name = {"shopsresultshort", "shoplist_top", "shopsresultmain", "shopsresultdphone", "shopsresultdeliver","shopsresultnotinternet"};
    private int mp3res, datares;

    MediaPlayer music;

    ArrayList<String> shopName = new ArrayList<>();
    ArrayList<String> shopPhone = new ArrayList<>();
    ArrayList<String> shopAddr = new ArrayList<>();
    ArrayList<String> shopDeliver = new ArrayList<>();
    ArrayList<String> shopPrimenum = new ArrayList<>();
    int nowShopStep = -1;

    int amStreamMusicVol;

    ConnectivityManager manager;
    NetworkInfo mobile;
    NetworkInfo wifi;

    Boolean canspeakshopname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        manager =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()){

        }else{

        }

        Intent intent = getIntent();
        nowTitle = intent.getStringExtra("title");
        nowTitleHangle = intent.getStringExtra("titlehagle");
        shopName = intent.getStringArrayListExtra("shopname");
        shopPhone = intent.getStringArrayListExtra("shopphone");
        shopAddr = intent.getStringArrayListExtra("shopaddr");
        shopDeliver = intent.getStringArrayListExtra("shopdeliver");
        shopPrimenum = intent.getStringArrayListExtra("shopprimenum");
        searched = intent.getStringExtra("searched");

        setTitle("메인화면 > " + nowTitleHangle + " > 점포검색 > " + searched);

        shopText = (TextView) findViewById(R.id.shopName);
        phoneText = (TextView) findViewById(R.id.phoneNum);

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        amStreamMusicVol = am.getStreamVolume(am.STREAM_MUSIC); //원래소리크기

        datares = getResources().getIdentifier("canspeakshopname", "array", getPackageName());
        canspeak = getResources().getStringArray(datares);
        for (int i=0; i<canspeak.length; i++) {
            if(nowTitle.equals(canspeak[i])){
                canspeakshopname = true;
                break;
            }
            if(i == canspeak.length-1){
                canspeakshopname = false;
            }
        }

        mp3res = getResources().getIdentifier(mp3name[0], "raw", getPackageName()); //short
        music = MediaPlayer.create(this, mp3res);
        music.setLooping(false);
        music.start();
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (shopName.size() < 10) {
                    mp3res = getResources().getIdentifier("shoplist_" + String.valueOf(shopName.size()), "raw", getPackageName());
                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                    music.setLooping(false);
                    music.start();
                    music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp3res = getResources().getIdentifier(mp3name[2], "raw", getPackageName());
                            music = MediaPlayer.create(getApplicationContext(), mp3res);
                            music.setLooping(false);
                            music.start();
                            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp3res = getResources().getIdentifier(mp3name[3], "raw", getPackageName());
                                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                                    music.setLooping(false);
                                    music.start();
                                }
                            });
                        }
                    });
                } else {
                    mp3res = getResources().getIdentifier("shoplist_10", "raw", getPackageName());
                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                    music.setLooping(false);
                    music.start();
                    music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp3res = getResources().getIdentifier(mp3name[2], "raw", getPackageName());
                            music = MediaPlayer.create(getApplicationContext(), mp3res);
                            music.setLooping(false);
                            music.start();
                            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp3res = getResources().getIdentifier(mp3name[3], "raw", getPackageName());
                                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                                    music.setLooping(false);
                                    music.start();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    TextToSpeech.OnInitListener shopnamelisten = new TextToSpeech.OnInitListener() {

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {// 초기화 성공
                myTTS.setLanguage(Locale.KOREA);
                myTTS.setPitch(0.9f);                //pitch 설정.
                myTTS.setSpeechRate(0.6f);        //rate 설정.

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                amStreamMusicVol = am.getStreamVolume(am.STREAM_MUSIC); //원래소리
                int amStreamMusicMaxVol = (int) (am.getStreamMaxVolume(am.STREAM_MUSIC)*0.5); //최대소리
                am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);

                myTTS.speak(shopName.get(nowShopStep), TextToSpeech.QUEUE_FLUSH, null);    //

            } else {// 초기화 실패
            }
        }
    };

    public void shopname_click(View view) {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        }catch (Exception e){

        }

        nowShopStep += 1;
        if (nowShopStep == shopName.size()) {
            nowShopStep = 0;
        }
        shopText.setText(shopName.get(nowShopStep));
        phoneText.setText(shopPhone.get(nowShopStep));

        if(canspeakshopname) {
            //Toast.makeText(getApplicationContext(), nowTitle, Toast.LENGTH_LONG).show();

            mp3res = getResources().getIdentifier(nowTitle + "shopname" + shopPrimenum.get(nowShopStep), "raw", getPackageName());
            music = MediaPlayer.create(getApplicationContext(), mp3res);
            music.setLooping(false);
            music.start();
            if (shopDeliver.get(nowShopStep).equals("Y")) {
                music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName()); //배달가능여부
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                    }
                });
            }
        }
        else{
            if (mobile.isConnected() || wifi.isConnected()) {

                myTTS = new TextToSpeech(getApplicationContext(), shopnamelisten); // 검색한 글자 TTS

                TimerTask myTask = new TimerTask() {        // 1초 뒤 이어서
                    @Override
                    public void run() {
                        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicVol, 0);   //볼륨 원래대로
                        if (shopDeliver.get(nowShopStep).equals("Y")) {
                            mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName()); //배달가능여부
                            music = MediaPlayer.create(getApplicationContext(), mp3res);
                            music.setLooping(false);
                            music.start();
                        }

                    }
                };
                Timer mTimer = new Timer();
                mTimer.schedule(myTask, 850);
            } else {
                if (shopDeliver.get(nowShopStep).equals("Y")) {
                    mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName()); //배달가능여부
                    music = MediaPlayer.create(getApplicationContext(), mp3res);
                    music.setLooping(false);
                    music.start();
                }
            }
        }
    }

    public void phonenumber_click(View view) {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        } catch (Exception e) {

        }

        if(nowShopStep == -1){

        }
        else{
            if (shopDeliver.get(nowShopStep).equals("Y")) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + shopPhone.get(nowShopStep)));

                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(callIntent);
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
        if(nowShopStep == -1){
            if (shopName.size() < 10) {
                mp3res = getResources().getIdentifier("shoplist_" + String.valueOf(shopName.size()), "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
                music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp3res = getResources().getIdentifier(mp3name[2], "raw", getPackageName());
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp3res = getResources().getIdentifier(mp3name[3], "raw", getPackageName());
                                music = MediaPlayer.create(getApplicationContext(), mp3res);
                                music.setLooping(false);
                                music.start();
                            }
                        });
                    }
                });
            } else {
                mp3res = getResources().getIdentifier("shoplist_10", "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
                music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp3res = getResources().getIdentifier(mp3name[2], "raw", getPackageName());
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp3res = getResources().getIdentifier(mp3name[3], "raw", getPackageName());
                                music = MediaPlayer.create(getApplicationContext(), mp3res);
                                music.setLooping(false);
                                music.start();
                            }
                        });
                    }
                });
            }
        }
        else {
            if (canspeakshopname) {
                //Toast.makeText(getApplicationContext(), nowTitle, Toast.LENGTH_LONG).show();

                mp3res = getResources().getIdentifier(nowTitle + "shopname" + shopPrimenum.get(nowShopStep), "raw", getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
                if (shopDeliver.get(nowShopStep).equals("Y")) {
                    music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName()); //배달가능여부
                            music = MediaPlayer.create(getApplicationContext(), mp3res);
                            music.setLooping(false);
                            music.start();
                        }
                    });
                }
            } else {
                if (mobile.isConnected() || wifi.isConnected()) {

                    myTTS = new TextToSpeech(getApplicationContext(), shopnamelisten); // 검색한 글자 TTS

                    TimerTask myTask = new TimerTask() {        // 1초 뒤 이어서
                        @Override
                        public void run() {
                            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicVol, 0);   //볼륨 원래대로
                            if (shopDeliver.get(nowShopStep).equals("Y")) {
                                mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName()); //배달가능여부
                                music = MediaPlayer.create(getApplicationContext(), mp3res);
                                music.setLooping(false);
                                music.start();
                            }

                        }
                    };
                    Timer mTimer = new Timer();
                    mTimer.schedule(myTask, 850);
                } else {
                    if (shopDeliver.get(nowShopStep).equals("Y")) {
                        mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName()); //배달가능여부
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
                    }
                }
            }
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(isfirst == false){
            mp3res = getResources().getIdentifier("shopsresultshort", "raw", getPackageName());
            music = MediaPlayer.create(getApplicationContext(), mp3res);
            music.setLooping(false);
            music.start();
        }
        isfirst = false;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            music.stop();
            music.release();
            myTTS.shutdown();
        }catch (Exception e){

        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            music.stop();
            music.release();
            myTTS.shutdown();
        }catch (Exception e){

        }
    }
}
