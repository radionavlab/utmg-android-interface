package utmg.android_interface.controller.button;

import android.view.View;

import java.util.function.Consumer;

import utmg.android_interface.activity.MainActivity;
import utmg.android_interface.model.util.SelectedTrajectory;
import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class SelectTrajectoryButtonHandler implements View.OnClickListener {

    private final Trajectory trajectory;
    private final SelectedTrajectory selectedTrajectory;
    private final MainActivity activity;

    public SelectTrajectoryButtonHandler(
            final Trajectory trajectory,
            final SelectedTrajectory selectedTrajectory,
            final MainActivity activity) {
        this.trajectory = trajectory;
        this.selectedTrajectory = selectedTrajectory;
        this.activity = activity;
    }

    @Override
    public void onClick(
            final View view) {
        // Change the selected trajectory
        this.selectedTrajectory.trajectory = this.trajectory;

        // Reinitialize the canvas controllers to control the new trajectory
        activity.initCanvasHandlers();
    }
}
