package linyeh.termproject;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MoneyCalculator extends AppCompatActivity {

    private Button button;
    private TextView txtTime;
    private TextView txtMoney;
    private int state = 0; // 0:開始計時，1:停止計時
    private long time_sec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_calculator);

        button = (Button) findViewById(R.id.button);
        txtTime = (TextView) findViewById(R.id.time);
        txtMoney = (TextView) findViewById(R.id.money);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state){
                    case 0:
                        time_sec = 16198;
                        button.setText("停止計時");
                        txtTime.setText("00 : 00 : 00");
                        txtMoney.setText("0 元");
                        timer.postDelayed(timerRunnable, 1000);
                        state = 1;
                        break;
                    case 1:
                        button.setText("開始計時");
                        timer.removeCallbacks(timerRunnable);
                        state = 0;
                        break;
                }
            }
        });
    }

    private Handler timer = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timer.postDelayed(timerRunnable, 1000);
            ++time_sec;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time_sec*1000);
            txtTime.setText((calendar.get(Calendar.HOUR)<10?"0"+Integer.toString(calendar.get(Calendar.HOUR)):Integer.toString(calendar.get(Calendar.HOUR))) + " : " +
                    (calendar.get(Calendar.MINUTE)<10?"0"+Integer.toString(calendar.get(Calendar.MINUTE)):Integer.toString(calendar.get(Calendar.MINUTE))) + " : " +
                            (calendar.get(Calendar.SECOND)<10?"0"+Integer.toString(calendar.get(Calendar.SECOND)):Integer.toString(calendar.get(Calendar.SECOND))));

            int cal_money = 0;
            long tmp_sec = time_sec;

            if(tmp_sec <= 30*60 && tmp_sec > 0){
                Log.d("1","1");
                cal_money = 0;
            }
            else if(tmp_sec <= 120*60 && tmp_sec > 30*60){
                tmp_sec -= 30*60;
                cal_money =  (int) (10 * ((int)(tmp_sec / 1800) + (int)(tmp_sec % 1800 != 0 ? 1 : 0)));
                Log.d("2",Integer.toString((tmp_sec % 1800 != 0 ? 1 : 0)));
            }
            else if(tmp_sec <= 240*60 && tmp_sec > 120*60){
                Log.d("3","3");
                tmp_sec -= 120*60;
                cal_money = (int) (10 * 3 + 20 * ((int)(tmp_sec / 1800) + (int)(tmp_sec % 1800 != 0 ? 1 : 0)));
            }
            else{
                Log.d("4","4");
                tmp_sec -= 240*60;
                cal_money = (int) (10 * 3 + 20 * 4 + 40 * ((int)(tmp_sec / 1800) + (int)(tmp_sec % 1800 != 0 ? 1 : 0)));
            }

            txtMoney.setText(Integer.toString(cal_money) + " 元");
        }
    };
}
