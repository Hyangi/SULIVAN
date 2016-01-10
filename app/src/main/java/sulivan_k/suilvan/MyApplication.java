package sulivan_k.suilvan;

import android.app.Application;

public class MyApplication extends Application{
    private boolean state;

    @Override
    public void onCreate() {
        //전역 변수 초기화
        state = false;
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setState(boolean state){
        this.state = state;
    }

    public boolean getState(){
        return state;
    }
}
