package com.cuiweiyou.headsetplayback;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ExitAppActivity extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ard","destroy---");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByDoubleClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 标识是否退出
    boolean isExit = false;

    private void exitByDoubleClick() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true;
            Toast.makeText(ExitAppActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 1000);
        } else {
            whenAppExit();
            finish();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000);
        }
    }

    public abstract void whenAppExit();
}


