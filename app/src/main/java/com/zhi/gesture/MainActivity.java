package com.zhi.gesture;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener{
    private GestureOverlayView gestures;
    private GestureLibrary library;
    private Gesture mGesture;
    private Button mBtnRecognize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestures = (GestureOverlayView) findViewById(R.id.gestures);
        mBtnRecognize = (Button) findViewById(R.id.btn_recognize);
        mBtnRecognize.setOnClickListener(this);

        library = GestureLibraries.fromRawResource(MainActivity.this, R.raw.gestures);
        library.load();
//        gestures.addOnGesturePerformedListener(new GesturePerformedListener());  // 手势画完的监听(一笔画完)
        gestures.addOnGestureListener(new GestureListener());   // 多笔手势监听
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_recognize:
                recognize(mGesture);
                gestures.clear(true);
                break;
        }
    }

    /**
     * 单笔手势监听
     */
    private final class GesturePerformedListener implements GestureOverlayView.OnGesturePerformedListener {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            recognize(gesture);
        }
    }

    /**
     * 多笔手势监听
     */
    private final class GestureListener implements GestureOverlayView.OnGestureListener {

        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {

        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {

        }

        @Override
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            mGesture = overlay.getGesture();
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {

        }
    }

    private void recognize(Gesture gesture) {
        ArrayList<Prediction> predictions = library.recognize(gesture);
        if(null == predictions){
            Toast.makeText(MainActivity.this, "没有匹配的手势", Toast.LENGTH_SHORT).show();
            return;
        }
        Prediction pre = predictions.get(0);
        if(pre.score < 6){
            Toast.makeText(MainActivity.this, "匹配度低", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pre.name.equals("close")){
            finish();
            return;
        }
        if(pre.name.equals("call")){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:18218364949"));
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());  // 退出应用
    }
}