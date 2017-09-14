package com.example.hacunamatata.exchangerates.aud;

/**
 * Created by Hacunamatata on 2016/12/30.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.hacunamatata.exchangerates.R;
import com.example.hacunamatata.exchangerates.common.ten_points;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.*;
import lecho.lib.hellocharts.view.LineChartView;

import java.util.ArrayList;

import java.util.List;


public class cn_au_settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { //set thresholds view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cn_au_settings);

        SharedPreferences thresholds = getSharedPreferences("thresholds",
                Activity.MODE_PRIVATE);

        String lower = thresholds.getString("aud_lower", "495");
        EditText text_lower = (EditText) findViewById(R.id.aud_lower);
        text_lower.setText(lower);
        String higher = thresholds.getString("aud_higher", "510");
        EditText text_higher = (EditText) findViewById(R.id.aud_higher);
        text_higher.setText(higher);


    }

    public void onStart() {                    //checkbox,interval,starting cn_au_service.class
        super.onStart();
        CheckBox box = (CheckBox) findViewById(R.id.checkBox);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                SharedPreferences thresholds = getSharedPreferences("thresholds",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = thresholds.edit();

                if (arg1) {
                    Intent intent = new Intent(cn_au_settings.this, cn_au_service.class);
                    startService(intent);
                    editor.putBoolean("aud_switch", true);
                    editor.commit();
                } else {

                    editor.putBoolean("aud_switch", false);
                    editor.commit();
                    if(thresholds.getBoolean("aud_switch",true)){
                    Toast.makeText(cn_au_settings.this, "fail", Toast.LENGTH_SHORT).show();}
                }

            }
        });

        SharedPreferences thresholds = getSharedPreferences("thresholds",
                Activity.MODE_PRIVATE);
        Boolean checked = thresholds.getBoolean("aud_switch", true);
        box.setChecked(checked);
        long interval = thresholds.getLong("aud_interval",30);
        TextView aud_interval = (TextView)findViewById(R.id.interval_value);
        aud_interval.setText(Long.toString(interval));
        SharedPreferences rates = getSharedPreferences("rates",
                Activity.MODE_PRIVATE);
        String value = rates.getString("aud_1", "null|null");
        TextView rate = (TextView) findViewById(R.id.cur_rate_value);
        rate.setText(value.split("\\|")[0]);
        TextView time = (TextView) findViewById(R.id.timestamp);
        time.setText(value.split("\\|")[1]);
        graph_btn();

        if (!checked) {
            return;
        }
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);//set an alarm to execute immediately
        Intent intent = new Intent(cn_au_settings.this, cn_au_service.class);
        PendingIntent sender = PendingIntent
                .getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExact(AlarmManager.RTC_WAKEUP, 0, sender);
    }

    @Override
    protected void onRestart() {       //update threshold and rate values
        super.onRestart();

    }

    public void save_btn(View view) {                 //save button for threshold and interval


        EditText lower = (EditText) findViewById(R.id.aud_lower);
        String s_l = lower.getText().toString().trim();
        EditText higher = (EditText) findViewById(R.id.aud_higher);
        String s_h = higher.getText().toString().trim();
        EditText interval = (EditText) findViewById(R.id.interval_value);
        SharedPreferences thresholds = getSharedPreferences("thresholds",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = thresholds.edit();
        try {
            long inter_value = Long.parseLong(interval.getText().toString());
            if(inter_value<=0){
                throw new Exception("");
            }
            editor.putLong("aud_interval", inter_value);
            editor.apply();
        } catch (Exception e) {
            Toast.makeText(this, "刷新间隔须为正整数", Toast.LENGTH_SHORT).show();
        }
        try {
            Float sl_f = Float.parseFloat(s_l);
            Float sh_f = Float.parseFloat(s_h);
            if (sl_f > sh_f) {
                throw new NullPointerException();
            }
            editor.putString("aud_lower", s_l);
            editor.putString("aud_higher", s_h);
            editor.apply();
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this,cn_au_service.class);
//            startService(intent);
        } catch (Exception e) {
            Toast.makeText(this, "输入必为数字，且左<=右", Toast.LENGTH_SHORT).show();
        }

    }
    public void graph_btn(){
        Context context = getApplicationContext();
        ten_points queue = new ten_points(context,"aud");

        ArrayList<String>[] dataPoints = queue.getAll();

        ArrayList<String> rates= dataPoints[0];
        ArrayList<String> timestamps= dataPoints[1];
        LineChartView lineChart = (LineChartView)findViewById(R.id.graph);

        int size = rates.size();
        List<PointValue> mPointValues = new ArrayList<PointValue>();
        List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
        try{
            for(int i=0;i<size;i++){

                mAxisXValues.add(new AxisValue(i).setLabel(timestamps.get(i)));
                mPointValues.add(new PointValue(i,Float.parseFloat(rates.get(i))));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLUE);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(10); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
//        data.setAxisXBottom(axisX); //x 轴在底部
        data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移

        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right= 10;
        lineChart.setCurrentViewport(v);
        lineChart.setOnValueTouchListener(new ValueTouchListener());

    }
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(cn_au_settings.this , "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

    public void onDestroy() {
        super.onDestroy();
    }


}