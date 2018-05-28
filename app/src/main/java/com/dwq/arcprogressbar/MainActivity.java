package com.dwq.arcprogressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ArcProgressBar arcProgressBar;
    private ValueAnimator valueAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcProgressBar = findViewById(R.id.arcProgressBar);

        initArcProgressBar();
        arcProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueAnimator.start();
            }
        });
    }

    private void initArcProgressBar() {
        valueAnimator = ValueAnimator.ofInt(0, 60);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                arcProgressBar.setProgress(animatedValue);
                arcProgressBar.setProgressDesc(String.valueOf(animatedValue + "%"));
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }


}
