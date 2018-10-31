package com.ziruk.base.zdsviewer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 宋棋安
 * on 2018/10/30.
 */
public class FilterDateTimePicker extends  RelativeLayout {
    private Context mContext = null;
    private String mLableText = "";
    private int mLayoutId = -1;
    private TextView textViewLable = null;
    private TextView textViewValueText = null;
    private Calendar value = Calendar.getInstance();
    private Boolean isFirstDraw = true;

    public FilterDateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mLableText = attrs.getAttributeValue((String)null, "lable");
        this.mLayoutId = attrs.getAttributeResourceValue((String)null, "layoutid", -1);
        if (this.mLayoutId == -1) {
            this.mLayoutId = com.ziruk.viewitems.R.layout.default_filter_datepicker_body;
        }

        this.value = Calendar.getInstance();
        this.value.set(Calendar.getInstance().get(1), Calendar.getInstance().get(2), Calendar.getInstance().get(5), 0, 0, 0);
        this.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new DatePickerDialog(FilterDateTimePicker.this.mContext, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                        FilterDateTimePicker.this.value.set(year, month, dayOfMonth);
                        FilterDateTimePicker.this.displayValue();
                    }
                }, FilterDateTimePicker.this.value.get(1), FilterDateTimePicker.this.value.get(2), FilterDateTimePicker.this.value.get(5));
                dialog.show();
            }
        });
        this.setWillNotDraw(false);
    }

    protected void onDraw(Canvas canvas) {
        if (this.isFirstDraw) {
            this.isFirstDraw = false;
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            ViewGroup view = (ViewGroup)inflater.inflate(this.mLayoutId, (ViewGroup)null, false);
            this.textViewLable = (TextView)view.findViewWithTag("textViewLable");
            this.textViewValueText = (TextView)view.findViewWithTag("textViewValueText");
            this.textViewLable.setText(this.mLableText);
            this.displayValue();
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(-1, -2);
            this.addView(view, lp2);
        }
    }

    public void setDate(Date value) {
        if (value != null) {
            this.value.setTime(value);
        }

        this.displayValue();
    }

    public void setDateAndAddDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(5, days);
        this.setDate(c.getTime());
    }

    public void setFirstDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(5, 1);
        this.setDate(c.getTime());
    }

    public void setLastDayOfLastMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(5, 1);
        c.add(5, -1);
        this.setDate(c.getTime());
    }

    public Date getDate() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String time = this.getStr();
        Date v = null;

        try {
            v = f.parse(time);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return v;
    }

    public String getStr() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String time = f.format(this.value.getTime());
        return time;
    }

    private void displayValue() {
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        String time = format0.format(this.value.getTime());
        if (this.textViewValueText != null) {
            this.textViewValueText.setText(time);
        }

    }
}

