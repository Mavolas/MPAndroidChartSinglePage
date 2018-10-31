package com.ziruk.base.zdsviewer.linechart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import zds.io.client.TagValue;
import zds.io.client.define.TagValueType;

/**
 * Created by 宋棋安
 * on 2018/10/29.
 */
public class LineChartActivityDemo extends Activity  {


    private Calendar cal;
    private LineChart chart;
    private  MultiLineChartBean mMultiLineChartBean = new MultiLineChartBean();

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_chart_view );

        chart = findViewById(R.id.lc_linechart);

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
            xAxis.setLabelCount(15, false);
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

                    SimpleDateFormat sf = new SimpleDateFormat( "yyyyMMdd" );

                    String format = sf.format( mTagValueList1.get( ( int ) value % mTagValueList1.size( ) ).time );

                    return DateUtil.formatDate(format);
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

        for (int i = 0; i < 40; i++) {
            float y = (float)( Math.random() * 40);

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


}


