package cc.ytdttj.bigblackclock44;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView clockTextView;
    private TextView batteryTextView;
    private Handler handler;
    private BroadcastReceiver batteryReceiver;
    private TextView sleepStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockTextView = findViewById(R.id.clockTextView);
        batteryTextView = findViewById(R.id.batteryTextView);
        handler = new Handler();
        sleepStatusTextView = findViewById(R.id.sleepStatusTextView);

        // 创建一个线程用于更新时钟
        Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                updateSleepStatus();
                handler.postDelayed(this, 1000); // 每秒更新一次
            }
        };

        handler.post(updateTimeRunnable);

        // 注册电池广播接收器
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                float batteryPercent = (level / (float) scale) * 100;

                String batteryStatusText = "";

                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    batteryStatusText = "正在充电：" + String.format(Locale.getDefault(), "%.2f%%", batteryPercent);
                } else {
                    batteryStatusText = "电量：" + String.format(Locale.getDefault(), "%.2f%%", batteryPercent);
                }

                batteryTextView.setText(batteryStatusText);
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    // 更新时钟文本
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        clockTextView.setText(currentTime);
    }

    // 更新睡眠状态文本和文本颜色
    private void updateSleepStatus() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        int currentHour = Integer.parseInt(sdf.format(new Date()));
        String statusText = "";

        if (currentHour >= 23 || currentHour < 6) {
            statusText = "该上床睡觉了";
            clockTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        } else {
            clockTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }

        sleepStatusTextView.setText(statusText);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册电池广播接收器
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
    }
}