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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        /* PieChart mPieChart = (PieChart) findViewById(R.id.piechart);

        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));

        mPieChart.startAnimation();
        */
        //https://github.com/PhilJay/MPAndroidChart/wiki
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(4f, "First"));
        entries.add(new PieEntry(8f, "Second"));
        entries.add(new PieEntry(6f, "Thirs"));

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
