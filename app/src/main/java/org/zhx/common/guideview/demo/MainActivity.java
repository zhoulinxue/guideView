package org.zhx.common.guideview.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.zhx.common.guideview.GuidanceLayout;
import org.zhx.common.guideview.GuideView;
import org.zhx.common.statubar.CommonStatusBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final GuideView guideVie = findViewById(R.id.guideView);
        final TextView textView = findViewById(R.id.target_view);
//        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int[] location=new int[2];
//                textView.getLocationInWindow(location);
//                guideVie.setCenterRect(new Rect(location[0],location[1],location[0]+textView.getWidth(),location[1]+textView.getHeight()));
//            }
//        });
        textView.post(new Runnable() {
            @Override
            public void run() {
                GuidanceLayout.Builder builder = new GuidanceLayout.Builder(MainActivity.this).anchorView(R.id.target_view);
                builder.Build().show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonStatusBar.acticity(this).whiteText().set();
    }
}