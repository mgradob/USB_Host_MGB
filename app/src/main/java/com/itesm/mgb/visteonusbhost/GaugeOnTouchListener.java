package com.itesm.mgb.visteonusbhost;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by miguel on 19/10/14.
 */
public class GaugeOnTouchListener implements View.OnTouchListener {

    private int x_delta, y_delta;
    public Main mainActivity;

    public GaugeOnTouchListener(Main mainActivity1){
        mainActivity = mainActivity1;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("VIEW", view.toString());

        int X = (int) motionEvent.getRawX();
        int Y = (int) motionEvent.getRawY();

        Log.d("VIEW", "X: " + X + ",Y: " + Y);

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                x_delta = X-layoutParams.leftMargin;
                y_delta = Y-layoutParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                lParams.leftMargin = X - x_delta;
                lParams.topMargin = Y - y_delta;
                lParams.rightMargin = -250;
                lParams.bottomMargin = -250;
                view.setLayoutParams(lParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        return true;
    }
}
