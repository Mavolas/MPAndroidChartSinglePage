package com.ziruk.base.zdsviewer.linechart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ziruk.base.R;
import com.ziruk.base.zdsviewer.base.BaseFragmentActivity;
import com.ziruk.base.zdsviewer.bean.FilterValues;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * 线图信息
 */
public class LineChartActivity extends BaseFragmentActivity {
    //region 变量定义
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerbar;
    private TextView imageViewHeaderFilter;
    private TextView textViewTitle;

    private LineChartFilterFragment filterFragment = null;
    private LineChartContentFragment contentFragment = null;
    public Boolean needRefresh = false;

    private Calendar mCalendar;

    public FilterValues filterValues = new FilterValues();
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone( TimeZone.getTimeZone("GMT+8:00"));

        //设置默认开始时间
        mCalendar.add( Calendar.DATE,-60 );

        filterValues.LastCheckInTimeFrom = mCalendar.getTime();
        filterValues.LastCheckInTimeTo = new Date(  );

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //setContentView(R.layout.activity_common_fragment_with_leftdrawer);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_common_header_btnback_txttitle_txtfilter);
        requestWindowFeature( Window.FEATURE_NO_TITLE);
        setContentView( R.layout.common_fragment_with_leftdrawer);

        //region 控件绑定
        textViewTitle = findViewById(R.id.textViewHeader);
        imageViewHeaderFilter = findViewById(R.id.imageViewHeaderFilter);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        //endregion

        textViewTitle.setText("线图信息");

        //参数获取
        //crud = IntentParaUtils.GetStringPara(this, "crud");

        //region 设定默认检索条件
        //this.filterValues.LastCheckInTimeFrom = DateUtils.setDays(new Date(), 1);
        //this.filterValues.LastCheckInTimeTo = new Date();
        //endregion

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        filterFragment = new LineChartFilterFragment();
        filterFragment.setValues(mDrawerLayout, this);
        transaction.replace(R.id.left_drawer, filterFragment);
        contentFragment = new LineChartContentFragment();
        transaction.replace(R.id.content_frame, contentFragment);
        contentFragment.setValues(this);
        transaction.commit();

        drawerbar = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.mipmap.ic_launcher,
                R.string.open, R.string.close) {

            //菜单打开
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                needRefresh = false;
            }
            // 菜单关闭
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (needRefresh==false)
                    return;
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                contentFragment = new LineChartContentFragment();
                contentFragment.setValues(LineChartActivity.this);
                transaction.replace(R.id.content_frame, contentFragment);
                transaction.commit();
            }

        };
        mDrawerLayout.setDrawerListener(drawerbar);

        imageViewHeaderFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDrawerLayout.openDrawer( Gravity.LEFT);
            }
        });

        Button btnBack = (Button ) findViewById(R.id.btnBack);
        if (btnBack !=null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPicClick(v);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*然后在碎片中调用重写的onActivityResult方法*/
        if (requestCode == 1)
            contentFragment.onActivityResult(requestCode, resultCode, data);
        else if (requestCode == 2)
            filterFragment.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onPause() {
        super.onPause();
        //IntentParaUtils.SaveStringPara(this, "crud", crud);
    }
}
