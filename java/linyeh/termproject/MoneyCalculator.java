package linyeh.termproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

public class MoneyCalculator extends AppCompatActivity {

    private Button button;
    private TextView txtTime;
    private TextView txtMoney;
    private int state = 0; // 0:開始計時，1:停止計時
    private long time_sec = 0;
    private int cal_money = 0;

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
                        time_sec = 0;
                        cal_money = 0;
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MoneyCalculator.this);
                        builder.setTitle("選擇視窗");
                        builder.setTitle("費用為：" + Long.toString(cal_money) + "\n是否搜尋最近站點？");
                        builder.setPositiveButton("是", DialogOnclickListener);
                        builder.setNegativeButton("否", DialogOnclickListener);
                        builder.show();
                        break;
                }
            }
        });
    }

    private DialogInterface.OnClickListener DialogOnclickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case BUTTON_POSITIVE:
                    Intent action = new Intent();
                    action.setClass(MoneyCalculator.this, StationListActivity.class);
                    startActivity(action);
                    break;
                case BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private Handler timer = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timer.postDelayed(timerRunnable, 1000);
            ++time_sec;
            txtTime.setText(((int)(time_sec/3600)<10?"0"+Integer.toString((int)(time_sec/3600)):Integer.toString((int)(time_sec/3600))) + " : " +
                    ((int)((time_sec/60)%60)<10?"0"+Integer.toString((int)((time_sec/60)%60)):Integer.toString((int)((time_sec/60)%60))) + " : " +
                            ((int)(time_sec%60)<10?"0"+Integer.toString((int)(time_sec%60)):Integer.toString((int)(time_sec%60))));

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
