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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class SearchShop extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;

    boolean typmod = false;

    boolean isfirst = true;

    InputMethodManager mgr;

    private EditText voicedata;

    String nowTitle, nowTitleHangle;
    String[] mp3name = {"searchshopshort","searchshop","modvoice","modkeyboardshort","internetnot","notstt"};
    private int mp3res,datares;
    MediaPlayer music;

    public class Message {
        public String shop_name = "집게리아";
        public String phone = "999-9999-9999";
        public String fulladdr ="831 Bottom feeder Lane";
        public String candeliver = "N";
        public String primnum = "0";
    }
    ArrayList<Message> nowshoplist = new ArrayList<Message>();
    ArrayList<String> searchedShopname = new ArrayList<>();
    ArrayList<String> searchedPhone = new ArrayList<>();
    ArrayList<String> searchedFulladdr = new ArrayList<>();
    ArrayList<String> searchedDiliver = new ArrayList<>();
    ArrayList<String> searchedPrime = new ArrayList<>();
    String[] nowshoptag;

    ConnectivityManager manager;
    NetworkInfo mobile;
    NetworkInfo wifi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchshop);

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

        setTitle("메인화면 > " + nowTitleHangle + " > 점포검색");

        MyApplication myApp = (MyApplication) getApplication();
        typmod = myApp.getState();

        datares = getResources().getIdentifier(nowTitle + "shoptag", "array", getPackageName());
        nowshoptag = getResources().getStringArray(datares);
        try{

            datares = getResources().getIdentifier(nowTitle + "shop", "xml", getPackageName());
            XmlPullParser parser = getResources().getXml(datares);

            Message currentM = new Message();
            while(parser.getEventType()!=XmlPullParser.END_DOCUMENT){
                String name;
                if(parser.getEventType()==XmlPullParser.START_TAG){
                    name = parser.getName();
                    if(name.equalsIgnoreCase(nowshoptag[0])){

                    }
                    else{

                        if(name.equalsIgnoreCase(nowshoptag[1])) {
                            currentM.shop_name = parser.nextText();
                        }
                        if(name.equalsIgnoreCase(nowshoptag[3])) {
                            currentM.phone = parser.nextText();
                        }
                        if(name.equalsIgnoreCase(nowshoptag[4])) {
                            currentM.fulladdr = parser.nextText();
                        }
                        if(name.equalsIgnoreCase(nowshoptag[5])) {
                            currentM.candeliver = parser.nextText();
                        }
                        if(name.equalsIgnoreCase(nowshoptag[6])) {
                            currentM.primnum = parser.nextText();
                        }
                    }
                    if(name.equalsIgnoreCase("store")){
                        nowshoplist.add(currentM);
                        currentM = new Message();
                    }
                }
                // 다음 태그로 이동
                parser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }


        voicedata.setOnEditorActionListener(new TextView.OnEditorActionListener() { //키보드 입력 모드일시시
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String reslt = voicedata.getText().toString();

                    searchedShopname.clear();
                    searchedPhone.clear();
                    searchedFulladdr.clear();
                    searchedDiliver.clear();
                    searchedPrime.clear();

                    for (int i = 0; i < nowshoplist.size(); i++) {
                        if (nowshoplist.get(i).fulladdr.contains(reslt) || nowshoplist.get(i).shop_name.contains(reslt)) {
                            searchedShopname.add(nowshoplist.get(i).shop_name);
                            searchedPhone.add(nowshoplist.get(i).phone);
                            searchedFulladdr.add(nowshoplist.get(i).fulladdr);
                            searchedDiliver.add(nowshoplist.get(i).candeliver);
                            searchedPrime.add(nowshoplist.get(i).primnum);
                        }
                    }
                    if(searchedFulladdr.size() == 0){
                        mp3res = getResources().getIdentifier("searchshopwrong", "raw", getPackageName());
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
                    else {
                 //       Toast.makeText(getApplicationContext(), searchedFulladdr.get(0), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Shops.class);
                        intent.putExtra("title", nowTitle);
                        intent.putExtra("titlehagle", nowTitleHangle);
                        intent.putExtra("searched",reslt);
                        intent.putExtra("shopname", searchedShopname);
                        intent.putExtra("shopphone", searchedPhone);
                        intent.putExtra("shopaddr", searchedFulladdr);
                        intent.putExtra("shopdeliver", searchedDiliver);
                        intent.putExtra("shopprimenum", searchedPrime);
                        startActivity(intent);
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
                } catch (Exception e) {

                }

                if (typmod) {
                    voicedata.setInputType(1);
                    voicedata.setText("");
                    mgr.showSoftInput(voicedata, InputMethodManager.SHOW_FORCED);
                } else {
                    //구글 STT
                    isfirst = true;
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ko-KR");

                    try {
                        startActivityForResult(intent, RESULT_SPEECH);
                        voicedata.setText("");
                    } catch (ActivityNotFoundException a) { //STT미지원
                        MyApplication myApp = (MyApplication) getApplication();
                        myApp.setState(true);
                        typmod = myApp.getState();

                        voicedata.setInputType(1);
                        mp3res = getResources().getIdentifier(mp3name[5], "raw", getPackageName());
                        music = MediaPlayer.create(getApplicationContext(), mp3res);
                        music.setLooping(false);
                        music.start();
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

                    searchedShopname.clear();
                    searchedPhone.clear();
                    searchedFulladdr.clear();
                    searchedDiliver.clear();
                    searchedPrime.clear();

                    for (int i = 0; i < nowshoplist.size(); i++) {
                        if(nowshoplist.get(i).fulladdr.contains(voicereslt) || nowshoplist.get(i).shop_name.contains(voicereslt)){
                            searchedShopname.add(nowshoplist.get(i).shop_name);
                            searchedPhone.add(nowshoplist.get(i).phone);
                            searchedFulladdr.add(nowshoplist.get(i).fulladdr);
                            searchedDiliver.add(nowshoplist.get(i).candeliver);
                            searchedPrime.add(nowshoplist.get(i).primnum);
                        }
                    }
                    if(searchedFulladdr.size() == 0){
                        mp3res = getResources().getIdentifier("searchshopwrong", "raw", getPackageName());
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
                    else {
                     //   Toast.makeText(getApplicationContext(), searchedFulladdr.get(0), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Shops.class);
                        intent.putExtra("title", nowTitle);
                        intent.putExtra("titlehagle", nowTitleHangle);
                        intent.putExtra("searched",voicereslt);
                        intent.putExtra("shopname", searchedShopname);
                        intent.putExtra("shopphone", searchedPhone);
                        intent.putExtra("shopaddr", searchedFulladdr);
                        intent.putExtra("shopdeliver", searchedDiliver);
                        intent.putExtra("shopprimenum",searchedPrime);
                        startActivity(intent);
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
            mp3res = getResources().getIdentifier("searchshopshort", "raw", getPackageName());
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
        if(typmod) {
            mp3res = getResources().getIdentifier("modkeyboardnot", "raw", getPackageName());
            music = MediaPlayer.create(getApplicationContext(), mp3res);
            music.setLooping(false);
            music.start();
        }
        else {
            mp3res = getResources().getIdentifier("searchshop", "raw", getPackageName());
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
                mp3res = getResources().getIdentifier(mp3name[4], "raw", getPackageName());
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
