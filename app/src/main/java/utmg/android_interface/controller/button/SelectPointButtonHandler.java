package utmg.android_interface.controller.button;

import android.view.View;

import utmg.android_interface.view.canvas.DrawingCanvas;

/**
 * Created by pwhitt24 on 10/25/17.
 */

public class SelectPointButtonHandler implements View.OnClickListener {
    private final DrawingCanvas canvas;

    public SelectPointButtonHandler (
            final DrawingCanvas canvas){
        this.canvas = canvas;
    }

    @Override
    public void onClick (
            final View view) {

    }
}

