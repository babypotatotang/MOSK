package com.example.mosk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

public class InfectionChartFragment<Likebutton> extends Fragment{
    ViewGroup viewGroup;
    Context mContext;
    TextView fragment_title;

    LikeButton mask_btn;

    ClickListener clickListener=new ClickListener();
    /*chart*/
    private PieChart chart;

    public void onAttach(Context context){
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup= (ViewGroup) inflater.inflate(R.layout.infection_fragment,container,false); //xml과 연결

        chart=viewGroup.findViewById(R.id.pieChart);
        fragment_title=viewGroup.findViewById(R.id.fragment1_title);
        mask_btn= viewGroup.findViewById(R.id.mask_btn);
        mask_btn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(mContext,"마스크 버튼을 클릭하셨습니다.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(mContext,"마스크 버튼을 클릭 취소 하셨습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        setChart(chart);


        return viewGroup;
    }

    public void setChart(PieChart chart){
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(48f);
        chart.setTransparentCircleRadius(51f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);

        // touch시 이벤트 발생 x
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(false);

        chart.animateY(1400, Easing.EaseInCirc);

        /*범례 표시 설정*/
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        ArrayList<PieEntry> entries=new ArrayList<>();

        entries.add(new PieEntry(60,"감염 위험률"));  // 감염되는 값을 여기에 저장
        entries.add(new PieEntry(40,"감염 안전율"));

        PieDataSet dataSet=new PieDataSet(entries,"코로나 감염확률");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors= new ArrayList<>();

        dataSet.setColors(new int[]{ColorTemplate.rgb("#ec4646"),ColorTemplate.rgb("#51c2d5")});

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        PieData data=new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);

        chart.invalidate();
        chart.animateXY(1400,1400);
    }


    /*클릭리스너 클래스*/
    private class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                default:
                    break;
            }
        }
    }
}