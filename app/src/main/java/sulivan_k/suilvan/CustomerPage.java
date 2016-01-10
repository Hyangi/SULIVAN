package sulivan_k.suilvan;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CustomerPage extends AppCompatActivity {


    boolean isfirst = true;
    String nowTitle, nowTitleHangle;


    String[] mp3name = {"customermain","",""};
    String[] nowCustomerData;
    private int mp3res,datares;
    MediaPlayer music;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerpage);



        Intent intent = getIntent();
        nowTitle = intent.getStringExtra("titlealpha");
        nowTitleHangle = intent.getStringExtra("titlehangle");

        // 커스터머 보이스파일 이름 불러오기
        datares = getResources().getIdentifier(nowTitle, "array", getPackageName());
        nowCustomerData = getResources().getStringArray(datares);
        mp3name[1] = nowCustomerData[0];    //this is 스파르타!
        mp3name[2] = nowCustomerData[1];    //short

        mp3res = getResources().getIdentifier(mp3name[1], "raw", getPackageName());
        music = MediaPlayer.create(this, mp3res);
        music.setLooping(false);
        music.start();
        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp3res = getResources().getIdentifier(mp3name[0],"raw",getPackageName());
                music = MediaPlayer.create(getApplicationContext(), mp3res);
                music.setLooping(false);
                music.start();
            }
        });



        setTitle("메인화면 > " + nowTitleHangle);

    }

    public void menu_onClick(View view) {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        }catch (Exception e){

        }
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra("title", nowTitle);
        intent.putExtra("titlehangle", nowTitleHangle);
        startActivity(intent);

    }
    public void shop_onClick(View view) {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        }catch (Exception e){

        }
        Intent intent = new Intent(this, SearchShop.class);
        intent.putExtra("title", nowTitle);
        intent.putExtra("titlehangle", nowTitleHangle);
        startActivity(intent);

    }
    public void repeat(View view) {
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(300);
        try {
            music.stop();
            music.release();
        }catch (Exception e){

        }
        mp3res = getResources().getIdentifier(mp3name[0],"raw",getPackageName());
        music = MediaPlayer.create(getApplicationContext(), mp3res);
        music.setLooping(false);
        music.start();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(isfirst == false){
            mp3res = getResources().getIdentifier(mp3name[2], "raw", getPackageName());
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
        }catch (Exception e){

        }

    }
}
