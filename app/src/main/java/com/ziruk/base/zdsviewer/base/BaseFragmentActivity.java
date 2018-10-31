package com.ziruk.base.zdsviewer.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;

public abstract class BaseFragmentActivity extends FragmentActivity {

    public static final int REQUEST_CODE_AUTO_LOGIN = 90301;
    public static final int RESULT_CODE_AUTO_LOGIN_OK = 90302;
    public static final String PARA_NAME_AUTO_LONGIN_RETURN_ACCOUNTINFO = "PARA_NAME_AUTO_LONGIN_RETURN_ACCOUNTINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FMApplication.getInstance().addActivity(this);
    }

    public void onBackPicClick(View v)
    {
        KeyEvent newEvent = new KeyEvent( KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_BACK);
        this.onKeyDown( KeyEvent.KEYCODE_BACK, newEvent);

        this.onBackPressed();
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode,  data);
        //resultCode就是在B页面中返回时传的parama，可以根据需求做相应的处理
    }
}
