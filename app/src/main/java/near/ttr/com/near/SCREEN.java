package near.ttr.com.near;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;

import java.util.Timer;
import java.util.TimerTask;

public class SCREEN extends AppCompatActivity {



    private void Latermove(){
        Timer time = new Timer();
        TimerTask tk = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                startActivity(new Intent("com.litreily.ChooseActivity"));
                //  startActivity(intent);
                finish();
            }
        };time.schedule(tk, 2000);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_screen);

        getSupportActionBar().hide(); //隐藏状态烂

        Latermove(); //两s后跳转
    }
}
