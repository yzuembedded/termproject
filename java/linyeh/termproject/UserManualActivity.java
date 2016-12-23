package linyeh.termproject;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class UserManualActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);
        TextView manual = (TextView) findViewById(R.id.textView);
        int i=0;
        manual.setText(Integer.toString(++i) + ".此應用程式為桃園uBike查詢系統。\n\n");
        manual.append(Integer.toString(++i) + ".首頁顯示之天氣資訊為桃園市天氣資訊。\n\n");
        manual.append(Integer.toString(++i) + ".租賃站地圖及租賃站列表中圖示顏色：綠色為正常租借、紅色為無可還車位、橘色為無可借車輛、黑色為暫停營運之站點\n\n");
        manual.append(Integer.toString(++i) + ".租賃站地圖為uBike站點之地圖資訊，初始定位為使用者所在地，若無法定位則預設為元智大學。\n\n");
        manual.append(Integer.toString(++i) + ".租賃站地圖中站點資訊包含站點名稱，可借、可歸還車輛數目，並且每五秒更新一次。\n\n");
        manual.append(Integer.toString(++i) + ".租賃站地圖中的氣象資訊為地圖所在地之資訊。\n\n");
        manual.append(Integer.toString(++i) + ".租賃站列表為uBike站點之條列式資訊，距離使用者所在地由近至遠排列。\n\n");
        manual.append(Integer.toString(++i) + ".租賃站列表中站點資訊包含站點名稱，可借、可歸還車輛數目，並且每五秒更新一次。\n\n");
        manual.append(Integer.toString(++i) + ".費率計算可依照官方計費方式計算費用，停止計時之後可選擇是否搜尋最近站點。\n\n");
        manual.append(Integer.toString(++i) + ".此應用程式由元智大學資訊工程學系1033305林岳儒、1033308葉庭安製作。\n\n");
        Spannable txt = new SpannableString(Integer.toString(++i) + ".特別感謝楊正仁教授、盧芃學長、鍾羽函學姊一學期以來努力的指導，這學期真的是受益良多，您們辛苦了！！\n\n");
        txt.setSpan(new ForegroundColorSpan(Color.RED), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        manual.append(txt);
    }
}
