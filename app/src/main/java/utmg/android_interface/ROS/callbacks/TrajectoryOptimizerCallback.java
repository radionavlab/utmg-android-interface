package utmg.android_interface.ROS.callbacks;

import android.app.Activity;
import android.content.Intent;

import java.util.function.Function;

import utmg.android_interface.ROS.IROSCallback;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class TrajectoryOptimizerCallback implements IROSCallback {

    private final int max;
    private int count;
    private final Function<Void, Void> callback;


    public TrajectoryOptimizerCallback(
            final int max,
            final Function callback) {
        this.max = max;
        this.callback = callback;
        this.count = 0;
    }

    @Override
    public void onFinished(boolean success) {
        this.count++;
        if(this.max == this.count){
            onFinishedAll(success);
        }
    }

    public void onFinishedAll(final boolean success) {
        callback.apply(null);
    }
}
