package utmg.android_interface.Canvases;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Date;

import android.content.SharedPreferences;

import org.ros.message.Time;

import utmg.android_interface.DataShare;
import utmg.android_interface.QuadUtils.Quad;


public class PreviewCanvas extends CanvasView {
    private static final float TOLERANCE = 5;
    SharedPreferences pref;
    int mode;

    public PreviewCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        pref = c.getSharedPreferences("Pref", 0);
        mode = pref.getInt("mode", 0);

        // instantiate x, y arrays

        // TODO: 7/27/2017 Convert optimized arrays to pixels, then create paths out of them
    }

    // override onSizeChanged



    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void callOnDraw() {
        super.draw(mCanvas);
    }




    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }








}
