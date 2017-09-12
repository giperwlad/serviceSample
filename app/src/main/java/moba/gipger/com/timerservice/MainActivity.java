package moba.gipger.com.timerservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimerServiceManager.getInstance().startTimer();
        TimerServiceManager.getInstance().getObservableTimer()
                .subscribe(seconds ->
                        Toast.makeText(this, seconds + "", Toast.LENGTH_SHORT).show()
                , throwable -> {
                });

        TimerServiceManager.getInstance().pauseTimer();
        TimerServiceManager.getInstance().disconnect();


    }
}
