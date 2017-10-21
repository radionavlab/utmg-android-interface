package utmg.android_interface.controller.button;

import android.view.View;

import java.util.function.Consumer;
import java.util.function.Function;

import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class SelectTrajectoryButtonHandler implements View.OnClickListener {

    private final Trajectory trajectory;
    private final Consumer<Trajectory> callback;

    public SelectTrajectoryButtonHandler(
            final Trajectory trajectory,
            final Consumer<Trajectory> callback) {
        this.trajectory = trajectory;
        this.callback = callback;
    }

    @Override
    public void onClick(
            final View view) {
        callback.accept(trajectory);
    }
}
