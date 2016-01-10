package sulivan_k.suilvan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Master on 2015-11-22.
 */
public class Splash extends Activity {

    @Override
    protected  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        try{
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
