package utmg.android_interface.controller.button;

import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.List;

import utmg.android_interface.ROS.ROSNodeMain;
import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

// TODO: Implement this class
public class SendAllTrajectoriesButtonHandler implements View.OnClickListener {
    public SendAllTrajectoriesButtonHandler(
            final List<Trajectory> trajectories,
            final ROSNodeMain nodeMain) {
    }

    @Override
    public void onClick(
            final View view) {

        // Display a snackbar at the bottom of the screen
        Snackbar.make(view, "Sending all trajectories...", Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }
}
