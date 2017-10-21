package utmg.android_interface.controller;

import android.widget.SeekBar;
import android.widget.TextView;

import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/17/17.
 */

public class AltitudeSeekBarHandler implements SeekBar.OnSeekBarChangeListener {

    private final TextView textView;
    private final Trajectory trajectory;

    public AltitudeSeekBarHandler(
            final TextView textView,
            final Trajectory trajectory) {
        this.textView = textView;
        this.trajectory = trajectory;
    }

    @Override
    public void onStopTrackingTouch(
            final SeekBar seekBar) {
        // Do nothing
    }

    @Override
    public void onStartTrackingTouch(
            final SeekBar seekBar) {
        // Do nothing
    }

    @Override
    public void onProgressChanged(
            final SeekBar seekBar,
            final int progress,
            final boolean fromUser) {
        final float value = ((float) progress / 100);
        this.textView.setText(Float.toString(value));
        trajectory.setAltitude(value);
    }
}
