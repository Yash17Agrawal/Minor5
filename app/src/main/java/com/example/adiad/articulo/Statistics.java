package com.example.adiad.articulo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
/*
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
*/

public class Statistics extends AppCompatActivity implements OnChartValueSelectedListener {

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        prefManager=new PrefManager(this);

        String percentage=prefManager.getPercentage();
        String[] parts = percentage.split(",");
        Log.e("parts",parts+" ");
        //penterainment + ","+pmusic +","+ ppeople +","+ psports+","+pnews+ "," + pecomm+ ","+ peducation+","+phealth + ","+ pothers +"," + ptech);

        float entertainment=Float.valueOf(parts[0]);
        float people=Float.valueOf(parts[1]);
        float sports=Float.valueOf(parts[2]);
        float news=Float.valueOf(parts[3]);
        float ecomm=Float.valueOf(parts[4]);
        float education=Float.valueOf(parts[5]);
        float health=Float.valueOf(parts[6]);
        float others=Float.valueOf(parts[7]);
        float tech=Float.valueOf(parts[8]);


        float total=entertainment+people+sports+news+ecomm+education+health+others+tech;





        /* PieChart mPieChart = (PieChart) findViewById(R.id.piechart);

        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));

        mPieChart.startAnimation();
        */
        //https://github.com/PhilJay/MPAndroidChart/wiki
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((entertainment*100)/total, "Entertainment"));
        entries.add(new PieEntry((people*100)/total, "People"));
        entries.add(new PieEntry((sports*100)/total, "Sports"));
        entries.add(new PieEntry((news*100)/total, "News"));
        entries.add(new PieEntry((ecomm*100)/total, "Ecommerce"));
        entries.add(new PieEntry((education*100)/total, "Education"));
        entries.add(new PieEntry((health*100)/total, "Health"));
        entries.add(new PieEntry((others*100)/total, "Others"));
        entries.add(new PieEntry((tech*100)/total, "Technology"));


        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        PieDataSet dataset = new PieDataSet(entries, "Facebook Interests");
        PieData data = new PieData(dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        //pieChart.setDescription("ert");
        pieChart.setData(data);

        pieChart.animateY(5000);
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        Log.e("yash", "welcome");
        if(e==null)
            Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),e.getY()+" "+e.getData()+" "+e.getX(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
