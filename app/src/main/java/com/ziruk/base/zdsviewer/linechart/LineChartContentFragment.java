package com.ziruk.base.zdsviewer.linechart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ziruk.base.R;
import com.ziruk.base.zdsviewer.bean.MultiLineChartBean;
import com.ziruk.base.zdsviewer.util.DateUtil;
import com.ziruk.viewitems.list.swipemenulist.SwipeMenuListView;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import zds.io.client.TagValue;
import zds.io.client.define.TagValueType;

/**
 * 列表内容Fragment：设施信息 / 车库信息
 */
public class LineChartContentFragment extends Fragment {

    private static final String TAG = LineChartContentFragment.class.getSimpleName();
    private Calendar cal;
    private LineChart chart;
    private MultiLineChartBean mMultiLineChartBean = new MultiLineChartBean();

    private LineChartActivity activity;

    List<TagValue> mTagValueList1 = null;
    List<TagValue> mTagValueList2 = null;
    List<TagValue> mTagValueList3 = null;

    private final int[] colors = new int[] {
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };

    private final String[] names = new String[] {
            "最大值",
            "最小值",
            "平均值"
    };
    private Button mButton;
    private Button mButton1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.activity_chart_view, container, false);

        chart = view.findViewById(R.id.lc_linechart);
        mButton = view.findViewById( R.id.btn_entry_add );
        mButton1 = view.findViewById( R.id.btn_entry_add_multi );


        activity = ( LineChartActivity ) getActivity();

        mTagValueList1 = generateData( );
        mTagValueList2 = generateData( );
        mTagValueList3 = generateData( );


        List<List<TagValue>> Multilist = new ArrayList <>(  );
        Multilist.add( mTagValueList1 );
        Multilist.add( mTagValueList2 );
        Multilist.add( mTagValueList3 );




        mMultiLineChartBean.setMultitagvalue( Multilist );
        mMultiLineChartBean.setNames( names );
        mMultiLineChartBean.setColors( colors );

        initChart();
        //showLineChart(mTagValueList1,"最大值",R.color.yellow);

        showMultiLineChart(mMultiLineChartBean);


        chart.setVisibleXRangeMaximum(20); //需要在设置数据源后生效

        chart.invalidate();


        mButton.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                addEntry();
            }
        } );

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode== 1) {

        }
    }

    public void setValues(LineChartActivity activity) {
        this.activity = activity;
    }

    //设置chart基本属性
    private void initChart() {
        // background color
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners
        chart.setDrawGridBackground(false);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);

        //动态加载数据
//        chart.notifyDataSetChanged(); // let the chart know it's data changed
//        chart.invalidate(); // refresh




        //=========================设置图例=========================
        // 像"□ xxx"就是图例
        Legend legend = chart.getLegend();
        //设置图例显示在chart那个位置 setPosition建议放弃使用了
        //设置垂直方向上还是下或中
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //设置水平方向是左边还是右边或中
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        //设置所有图例位置排序方向
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //设置图例的形状 有圆形、正方形、线
        legend.setForm(Legend.LegendForm.CIRCLE);
        //是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
        legend.setWordWrapEnabled(true);


        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();
            //设置x轴标签数
            xAxis.setLabelCount(10, false);
            //图表第一个和最后一个label数据不超出左边和右边的Y轴
            xAxis.setAvoidFirstLastClipping(true);

            //设置X轴显示位置
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);

            //设置X轴显示格式
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {

                    if ( "2".equals( activity.filterValues.FacilityType ) ){  //时线

                        SimpleDateFormat sf = new SimpleDateFormat( "dd日hh时" );
                        String format = sf.format( mTagValueList1.get( ( int ) value % mTagValueList1.size( ) ).time );
                        return format ;

                    }else {

                        SimpleDateFormat sf = new SimpleDateFormat( "MM月dd日" );
                        String format = sf.format( mTagValueList1.get( ( int ) value % mTagValueList1.size( ) ).time );
                        return format ;
                    }
                }
            });


        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);
            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);
            // axis range
            yAxis.setAxisMaximum(200f);
            yAxis.setAxisMinimum(-50f);
        }

//        Matrix m = new Matrix();//放大x轴
//        m.postScale(30f / 7f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
//        chart.getViewPortHandler().refresh(m, chart, false);//将图表动画显示之前进行缩放

//        chart.setVisibleXRangeMaximum(30);//需要在设置数据源后生效
//        chart.setVisibleXRange(10, 40);

//        chart.setVisibleYRangeMaximum(10, YAxis.AxisDependency.LEFT);


        //chart.setVisibleYRangeMaximum(10, YAxis.AxisDependency.RIGHT);


    }

    /**
     * 展示曲线
     *
     * @param dataList 数据集合
     * @param name     曲线名称
     * @param color    曲线颜色
     */
    public void showLineChart(List<TagValue> dataList, String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            TagValue data = dataList.get(i);
            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */

            Entry entry = new Entry(i, (float)data.value);
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
    }

    public void showMultiLineChart(MultiLineChartBean multiLineChartBean) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        for (int z = 0; z < 3; z++) {

            ArrayList<Entry> values = new ArrayList<>();

            for (int i = 0; i < multiLineChartBean.getMultitagvalue().get( z ).size(); i++) {

                TagValue tagValues = multiLineChartBean.getMultitagvalue().get( z ).get( i );

                Entry entry = new Entry(i, (float)tagValues.value);
                values.add(entry);
            }

            LineDataSet d = new LineDataSet(values, multiLineChartBean.getNames()[z]);

            initLineDataSet(d, multiLineChartBean.getColors()[z % multiLineChartBean.getColors().length], LineDataSet.Mode.LINEAR);

            dataSets.add(d);
        }

        LineData data = new LineData(dataSets);
        chart.setData(data);
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }

    private List<TagValue> generateData(){

        List<TagValue> tagValues = new ArrayList <>(  );

        cal = Calendar.getInstance();
        cal.setTimeZone( TimeZone.getTimeZone("GMT+8:00"));

        for (int i = 0; i < 100; i++) {
            float y = (float)( Math.random() * 80);

            cal.add( Calendar.DATE,i );

            TagValue tagValue = new TagValue( );

            tagValue.ID = UUID.randomUUID();
            tagValue.time = cal.getTime();
            tagValue.value = y;
            tagValue.valueType = TagValueType.Float;
            tagValues.add( tagValue );

        }

        return tagValues;

    }


    private void addEntry() {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency( YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }


    //region dp2px
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    //endregion
}
